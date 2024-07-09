# VLOG Project

## 기능 목록

1. #### 회원가입  
- [X] 회원가입 폼
- [X] 같은 ID , Email Check API
- [X] 회원 등록 기능
- [X] 회원 가입 후 로그인 폼으로 이동

2. #### 로그인
- [X] 로그인 폼
- [X] 로그인 기능
  - [X] 로그인 성공 후 ```/``` 로 이동
  - [X] 로그인 실패 후 다시 로그인 폼으로 이동 (오류메시지 출력)
- [X] Spring Security를 이용한 로그인 구현
  - [ ] Form Login
  - [X] JWT Login
  - [ ] OAuth2 로그인
- [X] 내 정보 조회
- [X] 내 정보 수정 (프로필 이미지, 댓글/업데이트 이메일 수신 여부, 이메일 ...)
- [X] 회원 탈퇴

3. #### 사이트 상단
- [ ] 사이트 로고가 좌측 상단에 보여짐
- [ ] 로그인 여부에 따른 우측 정보 표시
    - [ ] 로그인하지 않았을 경우 로그인 링크
    - [ ] 로그인했을 경우 사용자 이름
      - [ ] 사용자 이름 클릭시 설정, 해당 사용자 블로그 이동 링크, 블로그 이동 링크 , 로그아웃
      
4. #### 로그아웃
- [X] 로그아웃 기능

5. #### 메인페이지 (/)
- [ ] 블로그 글 보기
  - [X] 최신순
  - [X] 좋아요 많은 순
- [X] 페이징 처리 또는 무한 스크롤 구현
- [ ] 검색 기능
  - [ ] 제목  
  
6. #### 게시글
- [X] 게시글 작성
- [X] 게시글 출간
- [X] 게시글 수정 / 삭제
- [X] 게시글 상세 조회
- [X] 모든 게시글 조회
- [X] 내 모든 임시글 조회    

7. 좋아요 / 팔로우
- [X] 좋아요 등록 / 삭제
- [X] 팔로우 / 언팔로우

8. 댓글 / 답글
- [X] 댓글 등록/ 수정 / 삭제
- [X] 게시글의 댓글 조회