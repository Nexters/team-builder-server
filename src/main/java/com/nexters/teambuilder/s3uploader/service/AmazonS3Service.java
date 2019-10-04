package com.nexters.teambuilder.s3uploader.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class AmazonS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cdn.domain}")
    public String cdnDomain;

    private TransferManager transferManager;

//    private String tempDirPath = System.getProperty("java.io.tmpdir");
    private String tempDirPath = "/home/ec2-user/TeamBuilder/team-builder-server/tmp";
    public AmazonS3Service(TransferManager transferManager) {
        this.transferManager = transferManager;
    }


    public List<String> uploadImages(String targetPath, String filename, List<MultipartFile> multipartFileList) {
        String modifiedTargetPath = targetPath.replaceAll("^/*|/*$","");
        List<String> imageUrlList = new ArrayList<>();
        List<File> fileList = createFileListFrom(multipartFileList, filename);
        try {
            ObjectMetadataProvider metadataProvider = new ObjectMetadataProvider() {
                public void provideObjectMetadata(File file, ObjectMetadata metadata) {
                    if (Files.getFileExtension(file.getName()).equals(MediaType.WEBP.subtype())) {
                        metadata.setContentType(MediaType.WEBP.toString());
                    }
                }
            };
            File file = new File(FilenameUtils.getName(tempDirPath));
            try {
                if(!file.exists() || !file.isDirectory()) {
                    file.mkdir();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            MultipleFileUpload multipleFileUpload =
                    transferManager.uploadFileList(bucketName, modifiedTargetPath,
                            new File(FilenameUtils.getName(tempDirPath)), fileList, metadataProvider);
            multipleFileUpload.getSubTransfers().parallelStream()
                    .forEach(upload -> getKeyFromUploadResult(upload).map(imageUrlList::add));
        } catch (AmazonS3Exception ase) {
            log.error("Amazon S3 bucket " + bucketName + " access denied");
            log.error(ase.toString());
        } finally {
            fileList.forEach(File::delete);
        }

        return imageUrlList.stream()
                .map(imageS3Key -> getUploadedFileUrl(imageS3Key))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    private File createFileAtTempDirectoryFrom(MultipartFile multipartFile, String filename)  {
        String tmpDirectory = tempDirPath + "/";
        System.out.println(tmpDirectory);
        File file = new File(tmpDirectory, FilenameUtils.getName(filename));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException ioe) {
            log.error("Converting to temp file write fail : "
                    + multipartFile.getOriginalFilename() + " -> " + file.getName());
            ioe.getStackTrace();
        }
        return file;
    }

    private List<File> createFileListFrom(List<MultipartFile> multipartFileList, String filename) {
        if (multipartFileList.isEmpty()) {
            return Collections.emptyList();
        }

        if (multipartFileList.size() == 1) {
            return Collections.singletonList(
                    createAFileFrom(multipartFileList.get(0), filename));
        }

        return createFilesFrom(multipartFileList, filename);
    }

    private List<File> createFilesFrom(List<MultipartFile> multipartFiles, String filename) {
        return IntStream.range(0, multipartFiles.size())
                .mapToObj(i -> {
                    MultipartFile multipartFile = multipartFiles.get(i);
                    String originalFilename = multipartFile.getOriginalFilename();
                    if (Objects.isNull(originalFilename)) {
                        return createFileAtTempDirectoryFrom(multipartFile, alterFileName(filename, i));
                    }

                    String extension = Files.getFileExtension(originalFilename);
                    return createFileAtTempDirectoryFrom(multipartFile, alterFileName(filename, i, extension));
                }).collect(Collectors.toList());
    }

    private File createAFileFrom(MultipartFile multipartFile, String filename) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (Objects.isNull(originalFilename)) {
            return createFileAtTempDirectoryFrom(multipartFile, filename);
        }

        String extension = Files.getFileExtension(multipartFile.getOriginalFilename());
        return createFileAtTempDirectoryFrom(multipartFile, filename);
    }

    private Optional<String> getKeyFromUploadResult(Upload upload) {
        Optional<String> key = Optional.empty();
        try {
            key = Optional.of(upload.waitForUploadResult().getKey());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return key;
    }

    private String alterFileName(String filename, int index, String extension) {
        return filename + "-" + String.format("%03d", (index + 1)) + "." + extension;
    }

    private String alterFileName(String filename, int index) {
        return filename + "-" + String.format("%03d", (index + 1));
    }

    private String getUploadedFileUrl(String imageS3Key) {
        return "https://" + cdnDomain + "/" + imageS3Key;
    }
}
