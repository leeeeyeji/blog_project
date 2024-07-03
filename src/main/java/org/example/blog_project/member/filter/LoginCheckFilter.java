//package org.example.blog_project.member.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.extern.slf4j.Slf4j;
//import org.example.blog_project.member.SessionConst;
//import org.example.blog_project.member.UserContext;
//import org.springframework.util.PatternMatchUtils;
//
//import java.io.IOException;
//@Slf4j
//public class LoginCheckFilter implements Filter {
//    private static final String[] whiteList = {"/","/login","/register","/css/*","/api/login","/api/register"};
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String requestURI = httpRequest.getRequestURI();
//
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        try {
//            log.info("인증 체크 필터 시작 {}",requestURI);
//            if (isLoginCheckPath(requestURI)){
//                log.info("인증 체크 로직 실행 {}",requestURI);
//
//                HttpSession session = httpRequest.getSession(false);
//
//                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER)==null){
//                    log.info("미인증 사용자 요청 {}" ,requestURI);
//                    httpResponse.sendRedirect("/login?redirectURL="+requestURI);
//                    return;
//                }else {
//                    String memberId = String.valueOf(session.getAttribute(SessionConst.LOGIN_MEMBER));
//                    log.info("로그인 사용자 ID: {}", memberId); // 디버깅용 로그
//                    UserContext.setUserId(memberId);
//                }
//            }
//            chain.doFilter(request,response);
//        }catch (Exception e){
//            throw e;
//        }finally {
//            log.info("인증 체크 필터 종료 {}",requestURI);
//            UserContext.clear();
//        }
//    }
//
//    private boolean isLoginCheckPath(String requestURI){
//        return !PatternMatchUtils.simpleMatch(whiteList,requestURI);
//    }
//
//}
