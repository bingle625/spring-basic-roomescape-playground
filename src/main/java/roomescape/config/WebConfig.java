package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.member.LoginMemberArgumentResolver;
import roomescape.member.LoginMemberInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final LoginMemberArgumentResolver loginMemberArgumentResolver;
  private final LoginMemberInterceptor loginMemberInterceptor;

  public WebConfig(LoginMemberArgumentResolver loginMemberArgumentResolver, LoginMemberInterceptor loginMemberInterceptor) {
    this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    this.loginMemberInterceptor = loginMemberInterceptor;
  }

  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginMemberArgumentResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginMemberInterceptor).addPathPatterns("/admin");
  }
}
