package org.tlh.profile.util;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.tlh.profile.exception.OozieTaskException;

import java.util.Properties;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-30
 */
public final class OozieUtil {

    private OozieClient client;
    private String user;

    public OozieUtil(String oozieUrl, String user) {
        this.client = new OozieClient(oozieUrl);
        this.user = user;
    }

    /**
     * 提交和运行任务
     *
     * @param props
     * @return taskID
     */
    public String submitAndRunTask(Properties props) {
        try {
            Properties conf = client.createConfiguration();
            conf.putAll(props);
            //使用系统共享库
            conf.setProperty(OozieClient.USE_SYSTEM_LIBPATH, "true");
            //设置用户
            conf.setProperty(OozieClient.USER_NAME, user);
            conf.setProperty("security_enabled", "false");
            return this.client.run(conf);
        } catch (OozieClientException e) {
            throw new OozieTaskException("Submit Job Error.", e);
        }
    }

    /**
     * 提交任务
     *
     * @param props
     * @return taskID
     */
    public String submitTask(Properties props) {
        try {
            Properties conf = client.createConfiguration();
            conf.putAll(props);
            //使用系统共享库
            conf.setProperty(OozieClient.USE_SYSTEM_LIBPATH, "true");
            //设置用户
            conf.setProperty(OozieClient.USER_NAME, user);
            conf.setProperty("security_enabled", "false");
            return client.submit(conf);
        } catch (OozieClientException e) {
            throw new OozieTaskException("Submit Job Error.", e);
        }
    }

    /**
     * 启动任务
     *
     * @param taskId
     */
    public void startTask(String taskId) {
        try {
            this.client.start(taskId);
        } catch (OozieClientException e) {
            throw new OozieTaskException("Start Job Error.", e);
        }
    }

    /**
     * 暂停任务
     *
     * @param taskId
     */
    public void suspend(String taskId) {
        try {
            this.client.suspend(taskId);
        } catch (OozieClientException e) {
            throw new OozieTaskException("Suspend Job Error.", e);
        }
    }

    /**
     * 继续任务
     *
     * @param taskId
     */
    public void resumeTask(String taskId) {
        try {
            this.client.resume(taskId);
        } catch (OozieClientException e) {
            throw new OozieTaskException("Resume Job Error.", e);
        }
    }

    /**
     * 停止任务
     *
     * @param taskId
     */
    public boolean killTask(String taskId) {
        try {
            this.client.kill(taskId);
            return true;
        } catch (OozieClientException e) {
            throw new OozieTaskException("Remove Job Error.", e);
        }
    }

}
