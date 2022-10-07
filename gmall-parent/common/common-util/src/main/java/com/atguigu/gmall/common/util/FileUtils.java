package com.atguigu.gmall.common.util;

import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.csource.fastdfs.ClientGlobal;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author 黄梁峰
 *
 * 文件工具类
 *
 */
public class FileUtils {

    static {
        try {
            //加载文件
            ClassPathResource resource = new ClassPathResource("file.conf");
            //初始化
            ClientGlobal.init(resource.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String upload(MultipartFile file) throws Exception{
        String[] string = getStorageClient().upload_file(file.getBytes(),
                StringUtils.getFilenameExtension(file.getOriginalFilename()),
                null);
        System.out.println(string[0]);
        System.out.println(string[1]);
        return  string[0] + "/" + string[1];
    }

    /**
     * 文件下载
     *
     * @param group_name
     * @param remote_filename
     * @return
     * @throws Exception
     */
    public static byte[] download(String group_name, String remote_filename) throws Exception{
        byte[] bytes = getStorageClient().download_file(group_name, remote_filename);
        return bytes;
    }

    /**
     * 删除文件
     *
     * @param group_name
     * @param remote_filename
     * @return
     * @throws Exception
     */
    public static int delete(String group_name, String remote_filename) throws Exception{
        return getStorageClient().delete_file(group_name, remote_filename);
    }



    /**
     * 获取存储客户端
     *
     * @return
     * @throws Exception
     */
    private static StorageClient getStorageClient() throws Exception {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

}
