package org.example.blog_project.read_post;

import lombok.RequiredArgsConstructor;
import org.example.blog_project.post.Post;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadPostService {
    private final ReadPostRepository readPostRepository;

    public List<Post> getReadPosts(Long memberId){
        List<ReadPost> readPosts = readPostRepository.findByMember_memberIdOrderByReadAtDesc(memberId);
        return readPosts.stream().map(ReadPost::getPost).collect(Collectors.toList());
    }
}
