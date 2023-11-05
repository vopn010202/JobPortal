package vn.hcmute.springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hcmute.springboot.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

  Page<Company> findCompanyByName(String name, Pageable pageable);


  Company findByName(String name);
  @Query("SELECT j FROM Company j")
  Page<Company> findAllCompanies(Pageable pageable);
}
