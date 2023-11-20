package vn.hcmute.springboot.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.CandidateEducation;
import vn.hcmute.springboot.model.CandidateExperience;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.repository.CandidateEducationRepository;
import vn.hcmute.springboot.repository.CandidateExperienceRepository;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.CandidateEducationResponse;
import vn.hcmute.springboot.response.CandidateExperienceResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.service.ProfileService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

  private final UserRepository userRepository;
  private final CandidateEducationRepository candidateEducationRepository;
  private final CandidateExperienceRepository candidateExperienceRepository;
  @Override
  public MessageResponse updateUserProfile(ProfileUpdateRequest request) throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsernameIgnoreCase(userName)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy User"));
    user.setFullName(request.getFullName());
    user.setAboutMe(request.getAboutMe());
    user.setUsername(request.getEmail());
    user.setAvatar(request.getAvatar());
    user.setAddress(request.getAddress());
    user.setPosition(request.getPosition());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setBirthDate(request.getBirthdate());
    user.setLinkWebsiteProfile(request.getLinkWebsiteProfile());
    user.setGender(request.getGender());
    user.setCity(request.getCity());
    userRepository.save(user);
    return MessageResponse.builder()
            .message("cập-nhật-thông-tin-thành-công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  @Transactional
  public UserProfileResponse getUserProfile() {

    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
      CandidateEducationResponse educationResponse = null;
      if (user.getEducation() != null) {
        educationResponse = convertToCandidateEducationResponse(user.getEducation());
      }
      if(user.getBirthDate() != null){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String birthDate = user.getBirthDate().format(formatter);
      }
      else{
        user.setBirthDate(null);
      }
      return UserProfileResponse.builder()
              .id(user.getId())
              .fullName(user.getFullName())
              .aboutMe(user.getAboutMe())
              .avatar(user.getAvatar())
              .email(user.getUsername())
              .location(user.getLocation())
              .address(user.getAddress())
              .position(user.getPosition())
              .phoneNumber(user.getPhoneNumber())
              .linkWebsiteProfile(user.getLinkWebsiteProfile())
              .city(user.getCity())
              .education(educationResponse)
              .experience(user.getExperiences() != null ?
                      user.getExperiences().stream().map(this::convertToCandidateExperienceResponse).toList() :
                      Collections.emptyList())
              .gender(user.getGender())
              .skills(user.getSkills().stream().map(Skill::getTitle).toList())
              .build();
    } else {
      throw new NotFoundException("Không Tìm Thấy Profile của User");
    }
  }

  @Override
  public MessageResponse addEducation(AddEducationRequest request) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsernameIgnoreCase(userName)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy User"));
    if (user.getEducation() != null) {
      var existingEducation = candidateEducationRepository.findById(user.getEducation().getId())
              .orElseThrow(() -> new NotFoundException("Không tìm thấy Education"));
      existingEducation.setSchool(request.getSchool());
      existingEducation.setMajor(request.getMajor());
      existingEducation.setStartTime(LocalDate.parse(request.getStartDate()));
      existingEducation.setEndTime(LocalDate.parse(request.getEndDate()));
      candidateEducationRepository.save(existingEducation);
      return MessageResponse.builder()
              .message("Cập nhật thông tin thành công")
              .status(HttpStatus.OK)
              .build();
    } else {
      CandidateEducation candidateEducation = new CandidateEducation();
      candidateEducation.setSchool(request.getSchool());
      candidateEducation.setMajor(request.getMajor());
      candidateEducation.setStartTime(LocalDate.parse(request.getStartDate()));
      candidateEducation.setEndTime(LocalDate.parse(request.getEndDate()));
      candidateEducationRepository.save(candidateEducation);
      user.setEducation(candidateEducation);
      userRepository.save(user);
      return MessageResponse.builder()
              .message("Tạo mới thông tin thành công")
              .status(HttpStatus.OK)
              .build();

    }


  }

  @Override
  public MessageResponse addExperience(AddExperienceRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));
    if (request.getId() != 0) {
      var existingExperience = candidateExperienceRepository.findById(request.getId())
              .orElseThrow(() -> new NotFoundException("Không tìm thấy Experience"));
      existingExperience.setCompanyName(request.getCompanyName());
      existingExperience.setJobTitle(request.getJobTitle());
      existingExperience.setStartTime(LocalDate.parse(request.getStartDate()));
      existingExperience.setEndTime(LocalDate.parse(request.getEndDate()));
      candidateExperienceRepository.save(existingExperience);
      return MessageResponse.builder()
              .message("Cập nhật thông tin thành công")
              .status(HttpStatus.OK)
              .build();
    } else {
      CandidateExperience candidateExperience = new CandidateExperience();
      if (request.getCompanyName() != null) {
        candidateExperience.setCompanyName(request.getCompanyName());
        candidateExperience.setJobTitle(request.getJobTitle());
        candidateExperience.setStartTime(LocalDate.parse(request.getStartDate()));
        candidateExperience.setEndTime(LocalDate.parse(request.getEndDate()));
      }
      List<CandidateExperience> experience = user.getExperiences();
      experience.add(candidateExperience);
      candidateExperienceRepository.save(candidateExperience);
      userRepository.save(user);
    }
    return MessageResponse.builder()
            .message("Tạo mới thông tin thành công")
            .status(HttpStatus.OK)
            .build();

  }


  @Override
  public MessageResponse deleteEducation(Integer id) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));
    var education = candidateEducationRepository.findById(id);
    if (education.isPresent()) {
      user.setEducation(null);
      userRepository.save(user);
      candidateEducationRepository.deleteById(id);
    } else {
      throw new NotFoundException("Không tìm thấy Education");
    }
    return MessageResponse.builder()
            .message("Xóa thông tin học vấn thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse deleteExperience(Integer id) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));
    var experience = candidateExperienceRepository.findById(id).orElseThrow();
    if (experience.getId() != null) {
      for (var users : experience.getUsers()) {
        user.getExperiences().remove(experience);
        userRepository.save(user);
      }
      experience.getUsers().clear();
      candidateExperienceRepository.delete(experience);
      return new MessageResponse("Xóa thông tin kinh nghiêm thành công", HttpStatus.OK);

    }
    return new MessageResponse("Không có thông tin kinh nghiêm để xóa", HttpStatus.BAD_REQUEST);

  }

  @Override
  public MessageResponse writeAboutMe(AboutMeRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));
    user.setAboutMe(request.getAboutMe());
    userRepository.save(user);
    return MessageResponse.builder()
            .message("Cập nhật thông tin thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void addSkill(AddSkillRequest request) {

  }

  public CandidateExperienceResponse convertToCandidateExperienceResponse(CandidateExperience experience) {
    return CandidateExperienceResponse.builder()
            .id(experience.getId())
            .companyName(experience.getCompanyName())
            .jobTitle(experience.getJobTitle())
            .startTime(experience.getStartTime())
            .endTime(experience.getEndTime())
            .build();
  }

  public CandidateEducationResponse convertToCandidateEducationResponse(CandidateEducation education) {
    return CandidateEducationResponse.builder()
            .id(education.getId())
            .school(education.getSchool())
            .major(education.getMajor())
            .startTime(education.getStartTime())
            .endTime(education.getEndTime())
            .build();
  }


}
