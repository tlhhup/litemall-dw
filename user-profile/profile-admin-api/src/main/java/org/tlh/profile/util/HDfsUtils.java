package org.tlh.profile.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-23
 */
@Slf4j
public final class HDfsUtils implements Closeable {

    private FileSystem fs;

    public HDfsUtils(String url, String user) {
        try {
            URI uri = new URI(url);
            Configuration configuration = new Configuration();
            configuration.set("dfs.client.use.datanode.hostname", "true");
            fs = FileSystem.get(uri, configuration, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean uploadFile(InputStream is, String targetPath) {
        try {
            Path target = new Path(targetPath);
            FSDataOutputStream fos = fs.create(target, (short) 3);
            IOUtils.copyBytes(is, fos, 1024 * 100, true);
            return true;
        } catch (IOException e) {
            log.error("upload to hdfs error", e);
        }
        return false;
    }

    public boolean copyFileFromLocal(String sourcePath, String targetPath) {
        try {
            Path source = new Path(sourcePath);
            Path target = new Path(targetPath);
            fs.copyFromLocalFile(source, target);
            return true;
        } catch (IOException e) {
            log.error("upload to hdfs error", e);
        }
        return false;
    }

    public boolean deleteFile(String path) {
        try {
            Path target = new Path(path);
            return fs.deleteOnExit(target);
        } catch (IOException e) {
            log.error("delete file error", e);
        }
        return false;
    }


    @Override
    public void close() throws IOException {
        if (fs != null) {
            fs.close();
        }
    }
}
