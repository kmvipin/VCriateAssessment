package com.example.vcriateassessment.security;

import com.example.vcriateassessment.security.interf.MyUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${portfoliobuilder.allowed.origin}")
    private String allowedOrigin;

    @Autowired
    private PersonDetailService personDetailService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String origin = request.getHeader("Origin");

        if (allowedOrigin.equals(origin) || allowedOrigin.equals("*")) {
            // Origin is allowed, set the CORS header
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // Origin is not allowed, respond with an error or custom handling
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new RuntimeException("Something Went Wrong");
        }

        //get Token
        Optional<Cookie> cookieOptional = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(tempCookie -> tempCookie.getName().equals("authorization_token"))
                        .findFirst());
        Cookie cookie = null;
        if(cookieOptional.isPresent()){
            cookie = cookieOptional.get();
        }
        String requestToken= null;
        if(cookie != null)
            requestToken = cookie.getValue();

        if(requestToken == null){
            requestToken = request.getHeader("Authorization");
        }

        String username = null;
        String token = null;

        if(requestToken != null && requestToken.startsWith("Bearer")){
            token = requestToken.substring(7);
            try{
                username = this.jwtTokenHelper.getUsernameFromToken(token);
            }
            catch (IllegalArgumentException e){
                System.err.println(e.getMessage());
            }
            catch (ExpiredJwtException e){
                response.sendError(401,"Token Expired");
                System.err.println(e.getMessage());
            }
            catch (MalformedJwtException e){
                System.err.println(e.getMessage());
            }
        }
        else{
            throw new RuntimeException("it is null or does not start with bearer");
        }

        //Once we get the token, now validate
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            MyUserDetails userDetails = this.personDetailService.loadUserByUsername(username);
            if(this.jwtTokenHelper.validateToken(token,userDetails) && userDetails != null){
                //Now do Authentication

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,null,userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails
                        (new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else{
                throw new RuntimeException("Invalid Jwt Token");
            }
        }
        else{
            throw new RuntimeException("Username is null or Context is not null");
        }
        filterChain.doFilter(request,response);
    }
}