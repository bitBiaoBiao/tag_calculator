package com.mip.roaring.tagcalc.velocity.service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.mip.roaring.tagcalc.velocity.utils.Infix2SuffixCalculator;
import com.mip.roaring.tagcalc.velocity.vo.OperateResultVo;
import org.apache.commons.io.FileUtils;
import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author ZhengWenbiao
 * @Date 2019/5/31 15:47
 **/
@Service
public class CalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    private QueueContext.detailThread detailThread;

    @Resource
    private RoaringBitmapReadWrite rbReadWrite;

    @Value("tag.file.path")
    private String basePath;

    private static final String PREFIX = "key_";

    private static final String SUFFIX_TXT = ".txt";

    private static final String SUFFIX_DAT = ".dat";

    public void init(QueueContext.detailThread detailThread) {
        this.detailThread = detailThread;
        detailThread.start();
    }

    public OperateResultVo saveAudience(Long id, List<String> data) {
        OperateResultVo vo = new OperateResultVo();
        List<String> writeData = data.parallelStream().filter(k -> k != null).collect(Collectors.toList());
        Boolean flag = writeFile(writeData, getSavePath(id));
        if (flag == true)
            detailThread.queuePut(id);
        vo.setSuccess(flag);
        return vo;
    }

    public OperateResultVo handleRoaringId(String exp, List<Long> list) throws Exception {
        OperateResultVo vo = new OperateResultVo();
        String suffixString = Infix2SuffixCalculator.infixToSuffix(exp);    //中缀转为后缀表达式
        RoaringBitmap roaringBitmap = this.calculatorSuffix(suffixString);
        ConcurrentHashMap<Integer, String> resultMap = handleKeyFile(list);
        List<Integer> resultRoaring = IntStream.of(roaringBitmap.toArray()).boxed().collect(Collectors.toList());
        List<String> resultProfile = resultRoaring.parallelStream().map(k -> resultMap.get(k)).collect(Collectors.toList());
        vo.setData(resultProfile);
        vo.setSuccess(true);
        return vo;
    }

    private ConcurrentHashMap<Integer, String> handleKeyFile(Collection<Long> allIds) throws IOException {
        ConcurrentHashMap<Integer, String> result = new ConcurrentHashMap<>();
        for (Long id : allIds) {
            File file = new File(this.getKeyPath(id));
            if (!file.exists())
                break;
            try {
                Files.lines(Paths.get(this.getKeyPath(id))).parallel().filter(k -> k != null).forEach(k -> {
                    String[] s = k.split(":");
                    result.put(Integer.valueOf(s[0]), s[1]);
                });
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return result;
    }

    /**
     * 计算后缀表达式
     *
     * @param exp
     */
    private RoaringBitmap calculatorSuffix(String exp) throws Exception {
        String[] chars = exp.split("");
        Stack<RoaringBitmap> stack = new Stack<>();
        for (String c : chars) {
            switch (c) {
                case "&":
                case "|":
                    RoaringBitmap second = stack.pop();
                    RoaringBitmap first = stack.pop();
                    stack.push(calculator(first, second, c));
                    break;
                default:
                    stack.push(readBitmapFile(Long.parseLong(c)));
                    break;
            }
        }
        return stack.pop();
    }

    private RoaringBitmap calculator(RoaringBitmap first, RoaringBitmap second, String operator) {
        RoaringBitmap rb = new RoaringBitmap();
        if (operator.equals("|")) {
            rb = RoaringBitmap.or(first, second);
            return rb;
        }
        if (operator.equals("&")) {
            rb = RoaringBitmap.and(first, second);
            return rb;
        }
        return rb;
    }

    /**
     * 永久扫描任务，当有id传入时，创建该id所属于的rb集
     *
     * @param id
     * @throws Exception
     */
    public void scan(Long id) throws Exception {
        String profilePath = getSavePath(id);
        List<String> profiles = Files.lines(Paths.get(profilePath)).parallel().filter(k -> k != null).collect(Collectors.toList());
        createRoaringBitmap(id, profiles);
    }

    /**
     * 创建对应id的rb集以及key的文件
     *
     * @param id
     * @param profiles
     */
    private void createRoaringBitmap(Long id, List<String> profiles) {
        RoaringBitmap r = new RoaringBitmap();
        if (!profiles.isEmpty()) {
            List<String> listKey = profiles.parallelStream().map(k -> {
                /**
                 * hash出的int值可能为负数，但是roaringBitmap只存正数，范围 0 - （2^32-1）
                 * 存储key文件时，字符串形式存储，正则为中间:分隔，内容为 (正)负数: id     例如：554362715:18279618077
                 * 待 bitmap 逻辑计算后，最终将其 toArray 后再进行匹配
                 */
                int key = Hashing.murmur3_128().hashString(k, Charsets.UTF_8).asInt();
                r.add(key);
                StringBuffer sb = new StringBuffer(String.valueOf(key));
                sb.append(':');
                sb.append(k);
                return sb.toString();
            }).collect(Collectors.toList());
            Boolean file = writeFile(listKey, getKeyPath(id));
            if (file == true)
                rbReadWrite.write(r, getBitmapPath(id));
            logger.info("end to create {} roaringBitmap", id);
        }
    }

    private String getKeyPath(Long id) {
        return basePath + File.separator + id + File.separator + PREFIX + id + SUFFIX_TXT;
    }

    private String getSavePath(Long id) {
        return basePath + File.separator + id + File.separator + id + SUFFIX_TXT;
    }

    private String getBitmapPath(Long id) {
        return basePath + File.separator + id + File.separator + id + SUFFIX_DAT;
    }

    private RoaringBitmap readBitmapFile(Long id) throws Exception {
        String path = getBitmapPath(id);
        RoaringBitmap rb = rbReadWrite.read(path);
        return rb;
    }

    private Boolean writeFile(Collection collection, String path) {

        Boolean flag = false;
        try {
            File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            FileUtils.writeLines(file, collection);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            return flag;
        }
        return flag;
    }
}
