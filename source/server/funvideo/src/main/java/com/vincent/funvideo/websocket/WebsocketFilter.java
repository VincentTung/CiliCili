package com.vincent.funvideo.websocket;

import com.vincent.funvideo.config.shiro.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@Component
@WebFilter(filterName = "WebsocketFilter", urlPatterns = "/funvideo-api/video/barrage/{id}/{token}")
public class  WebsocketFilter implements Filter {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {



        if(((HttpServletRequest) servletRequest).getRequestURI().contains("funvideo-api/video/barrage")) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String token = ((HttpServletRequest) servletRequest).getHeader("Sec-WebSocket-Protocol");
//            int userId = jwtUtil.getUserId(token);
            response.setHeader("Sec-WebSocket-Protocol", token);
            System.out.print("=====filter====");
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
