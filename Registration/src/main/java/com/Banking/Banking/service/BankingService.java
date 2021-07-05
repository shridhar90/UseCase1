package com.Banking.Banking.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Banking.Banking.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Banking.Banking.entity.AccountDetails;
import com.Banking.Banking.entity.UserDetails;
import com.Banking.Banking.entity.FundTransferRequest;
import com.Banking.Banking.exception.NoFundsException;
import com.Banking.Banking.model.LoginRequest;
import com.Banking.Banking.model.SuccessResponse;
import com.Banking.Banking.repository.AccountRepositoryDto;
import com.Banking.Banking.repository.BankingRepositoryDto;
import com.Banking.Banking.repository.TransactionRepositoryDto;

@Service
public class BankingService {
	@Autowired
	private BankingRepositoryDto bankingRepository;

	@Autowired
	private AccountRepositoryDto accountRepositoryDto;

	@Autowired
	private TransactionRepositoryDto transactionRepositoryDto;
	static Logger logger = Logger.getLogger(BankingService.class.getName());

	public AccountDetails createBankingUser(UserDetails customerDetails) {
		UserDetails c = bankingRepository.save(customerDetails);
		logger.log(Level.INFO, () -> "c created: {0} " + c);
		AccountDetails account = createAccount(c.getUserId());
		return account;

	}

	public AccountDetails createAccount(Long customerId) {
		AccountDetails accountDetails = new AccountDetails("Savings Account", "Home Branch", customerId, "BANK001", 500.00d);
		AccountDetails a = accountRepositoryDto.save(accountDetails);
		logger.log(Level.INFO, () -> "a created: {0} " + a);
		return a;
	}

	public FundTransferRequest transferMoney(FundTransferRequest fundTransferRequest) {
		Long fromAccountNumber = fundTransferRequest.getFromAccount();
		Long toAccountNumber = fundTransferRequest.getToAccount();
		Double amount = fundTransferRequest.getAmount();
		AccountDetails fromAccount = accountRepositoryDto.findByAccountNumberEquals(fromAccountNumber);
		AccountDetails toAccount = accountRepositoryDto.findByAccountNumberEquals(toAccountNumber);
		if (fromAccount.getOpening_balance().compareTo(1.0d) >= 1
				&& fromAccount.getOpening_balance().compareTo(amount) >= 1) {
			Double balance = fromAccount.getOpening_balance() - amount;
			fromAccount.setOpening_balance(balance);
			AccountDetails fromAccountDetails = accountRepositoryDto.save(fromAccount);
			logger.log(Level.INFO, () -> "fromAccountDetails: {0} " + fromAccountDetails);
			Double toBalance = toAccount.getOpening_balance() + amount;
			toAccount.setOpening_balance(toBalance);
			AccountDetails toAccountDetails = accountRepositoryDto.save(toAccount);
			logger.log(Level.INFO, () -> "toAccountDetails: {0} " + toAccountDetails);
			Timestamp datetime = new Timestamp(System.currentTimeMillis());
			FundTransferRequest request = transactionRepositoryDto.save(new FundTransferRequest(amount,
					fromAccountNumber, toAccountNumber, datetime, fundTransferRequest.getType()));
			return request;
		}
		throw new NoFundsException();
	}

	public List<FundTransferRequest> getAccountHistory(Long accountNumber) {
		return transactionRepositoryDto.findByFromAccountOrToAccount(accountNumber, accountNumber);
	}

	public ResponseEntity<SuccessResponse> checkLogin(LoginRequest loginRequest) throws UserException {

		UserDetails customerDetails = bankingRepository.findByUserNameAndPassword(loginRequest.getUserName(),
				loginRequest.getPassword());
		
		if (null != customerDetails) {
			return new ResponseEntity<>(new SuccessResponse("success", "Logged in Successfully"), HttpStatus.OK);
		}
		else {
			//return new ResponseEntity<>(new SuccessResponse("failure", "Login not successful"), HttpStatus.BAD_REQUEST);
         throw new  UserException("unauthorized User");
		}
	}
}
