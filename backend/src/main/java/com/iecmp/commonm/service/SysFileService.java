package com.iecmp.commonm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iecmp.commonm.entity.SysFile;
import com.iecmp.commonm.vo.FileUploadBatchVO;
import com.iecmp.commonm.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件业务接口（{@code sys_file}）。
 */
public interface SysFileService extends IService<SysFile> {

    /** 8-01 单文件上传。 */
    FileUploadVO upload(MultipartFile file, String bizType, Long userId);

    /** 8-02 多文件上传。 */
    List<FileUploadBatchVO> uploadBatch(List<MultipartFile> files, String bizType, Long userId);

    /** 8-03 删除文件。 */
    void deleteFile(Long id, Long userId, boolean superAdmin);
}