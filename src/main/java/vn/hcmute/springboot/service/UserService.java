package vn.hcmute.springboot.service;


import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.SignUpRequest;
import vn.hcmute.springboot.response.JwtResponse;

public interface UserService {
  JwtResponse login(LoginRequest userLogin) throws Exception;
  User registerUser(SignUpRequest request);

}