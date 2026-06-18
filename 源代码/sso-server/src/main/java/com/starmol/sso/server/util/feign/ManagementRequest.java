package com.starmol.sso.server.util.feign;

import com.starmol.sso.server.pojo.dto.ResponseWrapper;
import com.starmol.sso.server.pojo.dto.User;
import com.starmol.sso.server.util.feign.param.UpdatePasswordDTO;
import com.starmol.sso.server.util.feign.param.UserLoginRequest;
import com.starmol.sso.server.util.feign.param.UserLoginResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "management-client", url = "${sso.oauth.management-url}")
public interface ManagementRequest {

    @PostMapping("/users/login/{captchaId}")
    UserLoginResponse login(@PathVariable("captchaId") String captchaId, @RequestHeader("X-Real-IP") String ip, @RequestHeader("X-Real-PORT") String port, @RequestBody UserLoginRequest info);

    @PostMapping(value = "/users/list-by-ids", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseWrapper<List<User>> listUserByIds(@RequestBody List<String> userIds);

    @PostMapping(value = "/users/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseWrapper<String> updatePassword(UpdatePasswordDTO updatePasswordDTO);

    @GetMapping(value = "/captcha/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<InputStreamResource> captchaGenerate();
}
