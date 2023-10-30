package vn.hcmute.springboot.controller;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.service.CompanyService;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
  private final CompanyService companyService;

  @GetMapping()
  public ResponseEntity<CompanyResponse> getAllCompanies(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Company> allCompany =  companyService.listAllCompany(page,size);
    if(allCompany.isEmpty()) {
      return new ResponseEntity<>(new CompanyResponse("Không tìm thấy công ty",HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(new CompanyResponse(allCompany), HttpStatus.OK);
  }

  @GetMapping("/findByName")
  public ResponseEntity<CompanyResponse> findCompanyByName(@RequestParam("name") String name,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Company> company = companyService.findCompanyByName(name,page,size);
    if(company == null) {
      return new ResponseEntity<>(new CompanyResponse("Không tìm thấy công ty",HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(new CompanyResponse(company), HttpStatus.OK);
  }

  @GetMapping("{id}")
  public ResponseEntity<Company> findCompanyById(@PathVariable Integer id){
    var company = companyService.findCompanyById(id);
    if(company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(company,HttpStatus.OK);
  }
}
