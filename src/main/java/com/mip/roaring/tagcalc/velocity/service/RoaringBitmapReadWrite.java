package com.mip.roaring.tagcalc.velocity.service;

import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @Author ZhengWenbiao
 * @Date 2019/3/21 11:18
 **/
@Service
public class RoaringBitmapReadWrite {

    private static final Logger logger = LoggerFactory.getLogger(RoaringBitmapReadWrite.class);

    public RoaringBitmap read(String path) throws Exception{

        RoaringBitmap r = new RoaringBitmap();
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(path));
            r.deserialize(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("bitmap file fail for read from ={}", path);
            throw e;
        }
        return r;
    }

    public void write(RoaringBitmap r, String path) {

        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
            r.runOptimize();
            r.serialize(out);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("bitmap file fail to write ={}", path);
        }
    }
}