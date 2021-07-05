package com.Banking.Banking.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Banking.Banking.entity.UserDetails;



public interface BankingRepositoryDto extends JpaRepository<UserDetails, Long>{

	UserDetails findByUserNameAndPassword(String userName, String password);

}
