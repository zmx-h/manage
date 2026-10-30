package com.iecmp.commonm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iecmp.common.exception.BusinessException;
import com.iecmp.common.properties.UploadProperties;
import com.iecmp.common.result.ResultCode;
import com.iecmp.commonm.entity.SysFile;
import com.iecmp.commonm.enums.CommonErrorCode;
import com.iecmp.commonm.mapper.SysFileMapper;
import com.iecmp.commonm.service.SysFileService;
import com.iecmp.commonm.vo.FileUploadBatchVO;
import com.iecmp.commonm.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 文件业务实现（{@code sys_file}）。
 *
 * <p>当前默认写入本地目录，返回 URL 由 {@code upload.url-prefix} 拼接；
 * 生产环境可将该前缀替换为 OSS/CDN 地址，表结构与前端契约不变。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
        implements SysFileService {

    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Set<String> DOC_EXT = Set.of("pdf", "doc", "docx", "xls", "xlsx");
    private static final Set<String> BIZ_TYPES = Set.of(
            "avatar", "asset", "honor", "budget", "seat", "notice", "video", "common");
    private static final DateTimeFormatter YEAR_FMT = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MM");

    private final UploadProperties uploadProperties;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUploadVO upload(MultipartFile file, String bizType, Long userId) {
        SysFile saved = store(file, bizType, userId);
        return toUploadVO(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileUploadBatchVO> uploadBatch(List<MultipartFile> files, String bizType, Long userId) {
        if (files == null || files.isEmpty()) {
            throw error(CommonErrorCode.E80001);
        }
        if (files.size() > 9) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "单次最多上传 9 个文件");
        }
        String normalizedBizType = normalizeBizType(bizType);
        List<FileUploadBatchVO> result = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            SysFile saved = store(file, normalizedBizType, userId);
            FileUploadBatchVO vo = new FileUploadBatchVO();
            vo.setFileId(saved.getId());
            vo.setName(saved.getName());
            vo.setUrl(saved.getUrl());
            vo.setSize(saved.getSize());
            vo.setContentType(saved.getContentType());
            vo.setBizType(saved.getBizType());
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id, Long userId, boolean superAdmin) {
        SysFile file = getById(id);
        if (file == null) {
            throw error(CommonErrorCode.E80004);
        }
        if (!superAdmin && (file.getUploadUserId() == null || !file.getUploadUserId().equals(userId))) {
            throw error(CommonErrorCode.E80006);
        }
        if (isReferenced(file)) {
            throw error(CommonErrorCode.E80005);
        }
        if (!removeById(id)) {
            throw error(CommonErrorCode.E80004);
        }
        deleteLocalFile(file.getObjectKey());
    }

    private SysFile store(MultipartFile file, String bizType, Long userId) {
        if (file == null || file.isEmpty()) {
            throw error(CommonErrorCode.E80001);
        }
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        String originalName = safeOriginalName(file.getOriginalFilename());
        String ext = extensionOf(originalName);
        boolean image = IMAGE_EXT.contains(ext);
        if (!image && !DOC_EXT.contains(ext)) {
            throw error(CommonErrorCode.E80002);
        }
        long maxSize = image ? uploadProperties.getMaxImageSize() : uploadProperties.getMaxDocSize();
        if (file.getSize() > maxSize) {
            throw error(CommonErrorCode.E80003);
        }
        String normalizedBizType = normalizeBizType(bizType);
        LocalDate today = LocalDate.now();
        String objectKey = normalizedBizType + "/" + today.format(YEAR_FMT) + "/"
                + today.format(MONTH_FMT) + "/" + UUID.randomUUID().toString().replace("-", "")
                + "." + ext;

        Path root = localRoot();
        Path target = root.resolve(objectKey).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件路径非法");
        }
        try {
            Files.createDirectories(target.getParent());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("保存上传文件失败: {}", objectKey, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件保存失败");
        }

        SysFile entity = new SysFile();
        entity.setName(originalName);
        entity.setUrl(buildUrl(objectKey));
        entity.setObjectKey(objectKey);
        entity.setBucket("local");
        entity.setSize(file.getSize());
        entity.setContentType(file.getContentType());
        entity.setBizType(normalizedBizType);
        entity.setUploadUserId(userId);
        try {
            if (!save(entity)) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件记录保存失败");
            }
        } catch (RuntimeException e) {
            deleteLocalFile(objectKey);
            throw e;
        }
        return entity;
    }

    private FileUploadVO toUploadVO(SysFile file) {
        FileUploadVO vo = new FileUploadVO();
        vo.setFileId(file.getId());
        vo.setName(file.getName());
        vo.setUrl(file.getUrl());
        vo.setSize(file.getSize());
        vo.setContentType(file.getContentType());
        vo.setBizType(file.getBizType());
        return vo;
    }

    private String normalizeBizType(String bizType) {
        String value = StringUtils.hasText(bizType) ? bizType.trim().toLowerCase(Locale.ROOT) : "common";
        if (value.length() > 32 || !BIZ_TYPES.contains(value)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "bizType 取值不合法");
        }
        return value;
    }

    private String safeOriginalName(String name) {
        if (!StringUtils.hasText(name)) {
            return "file";
        }
        String normalized = name.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        String result = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        return result.isBlank() ? "file" : result;
    }

    private String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 && dot < name.length() - 1
                ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    }

    private Path localRoot() {
        return Path.of(uploadProperties.getLocalDir()).toAbsolutePath().normalize();
    }

    private String buildUrl(String objectKey) {
        String prefix = uploadProperties.getUrlPrefix();
        if (!StringUtils.hasText(prefix)) {
            prefix = "/api/upload";
        }
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix + "/" + objectKey;
    }

    private void deleteLocalFile(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return;
        }
        Path root = localRoot();
        Path target = root.resolve(objectKey).normalize();
        if (!target.startsWith(root)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("删除本地文件失败: {}", target, e);
        }
    }

    private boolean isReferenced(SysFile file) {
        long refs = 0;
        String url = file.getUrl();
        refs += countUrl("sys_user", "avatar_url", url);
        refs += countUrl("asset_info", "image_urls", url);
        refs += countUrl("honor_alumni", "avatar_url", url);
        refs += countUrl("honor_record", "certificate_url", url);
        refs += countUrl("seat_area", "plan_image_url", url);
        refs += countUrl("sys_notice", "content", url);
        refs += countUrl("sys_video", "cover_url", url);
        refs += countId("budget_apply", "attach_file_id", file.getId());
        refs += countId("budget_expense", "voucher_file_id", file.getId());
        return refs > 0;
    }

    private long countUrl(String table, String column, String url) {
        if (!StringUtils.hasText(url)) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " LIKE ?"
                + (hasDeletedColumn(table) ? " AND deleted = 0" : "");
        Long count = jdbcTemplate.queryForObject(sql, Long.class, "%" + url + "%");
        return count == null ? 0 : count;
    }

    private long countId(String table, String column, Long fileId) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?"
                + (hasDeletedColumn(table) ? " AND deleted = 0" : "");
        Long count = jdbcTemplate.queryForObject(sql, Long.class, fileId);
        return count == null ? 0 : count;
    }

    private boolean hasDeletedColumn(String table) {
        return !"sys_oper_log".equals(table);
    }

    private BusinessException error(CommonErrorCode code) {
        return new BusinessException(code.getCode(), code.getMsg());
    }
}