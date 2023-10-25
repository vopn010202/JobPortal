package vn.hcmute.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
@Data
@Builder
public class MessageResponse {
  private String message;
  private HttpStatus status;

}
