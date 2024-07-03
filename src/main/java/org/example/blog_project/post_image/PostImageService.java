package org.example.blog_project.post_image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog_project.post.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostImageService {
    private final PostImageRepository postImageRepository;

    @Transactional
    public void createPostImage(Post post, List<MultipartFile> files){
        try{
            String[] postImageUrls = saveImages(files);
            for (int i = 0;i < files.size();i++){
                PostImage postImage = PostImage.builder()
                        .imageUrl(postImageUrls[i])
                        .orderIndex(i)
                        .post(post)
                        .build();
                log.info("이미지 등록중"+postImage.getImageUrl());
                postImageRepository.save(postImage);
                post.getPostImageList().add(postImage);
            }


        } catch (IOException e){
            log.info(e.getMessage());
        }
    }

    @Value("${content.file.path}")
    private String contentFilePath;


    private String[] saveImages(List<MultipartFile> files) throws IOException {
        String[] savedFilePaths = new String[files.size()];

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = UUID.randomUUID().toString() + fileExtension;
            String savedFilePath = contentFilePath + File.separator + savedFileName;

            File savedFile = new File(savedFilePath);
            file.transferTo(savedFile);

            savedFilePaths[i] = "/content_images/" + savedFileName; // URL path
        }
        log.info("이미지 저장 메소드");
        return savedFilePaths;
    }
    @Transactional
    public void deletePostImages(Post post) {
        List<PostImage> images = post.getPostImageList();
        if (images != null && !images.isEmpty()) {
            for (PostImage image : images) {
                File file = new File(contentFilePath + File.separator + image.getImageUrl().substring("/content_images/".length()));
                if (file.exists() && !file.delete()) {
                    log.error("Failed to delete image file: " + file.getPath());
                }
            }
            postImageRepository.deleteAll(images);
            log.info("이미지삭제완료");
            post.getPostImageList().clear(); // Clear the list after deleting images
        }
    }

    @Transactional
    public void updatePostImages(Post post, List<MultipartFile> files) throws IOException {
        // 기존 이미지 삭제
        deletePostImages(post);

        //log.info("files 상태 어떰??={}",files.get(0));
        // 새 이미지 저장
        if (files != null && !files.isEmpty()) {
            log.info("파일존재");
            createPostImage(post, files);  // 재사용 이미지 생성 메서드
        }
    }


}
