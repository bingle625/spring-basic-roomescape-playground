package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.member.LoginMemberArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final LoginMemberArgumentResolver loginMemberArgumentResolver;

  public WebConfig(LoginMemberArgumentResolver loginMemberArgumentResolver) {
    this.loginMemberArgumentResolver = loginMemberArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(this.loginMemberArgumentResolver);
  }
}
