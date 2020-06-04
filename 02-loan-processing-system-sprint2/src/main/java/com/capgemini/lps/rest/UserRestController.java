package com.capgemini.lps.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.lps.entity.Applicant;
import com.capgemini.lps.entity.JwtResponse;
import com.capgemini.lps.entity.User;
import com.capgemini.lps.exception.EmailNotFoundException;
import com.capgemini.lps.exception.UserNotFoundException;
import com.capgemini.lps.response.Response;
import com.capgemini.lps.security.UserDetailsImpl;
import com.capgemini.lps.service.ApplicantService;
import com.capgemini.lps.service.JwtUtil;
import com.capgemini.lps.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserRestController {
	@Autowired
	private UserService userService;

	public UserRestController(UserService theUserService) {
		userService = theUserService;
	}

	@Autowired
	private ApplicantService applicantService;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user) throws Exception {

		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
		} catch (DisabledException de) {
			throw new EmailNotFoundException("Invalid email or password");

		} catch (BadCredentialsException bce) {
			throw new EmailNotFoundException("Invalid email or password");

		} // End of try catch

		final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());

		final String jwt = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUserId(), userDetails.getFullName(),
				userDetails.getEmail(), userDetails.getRole()));// doubt
	}// End of login()

	@GetMapping("/getUser")
	public List<User> findAll() {

		return userService.findAll();
	}
	@GetMapping("/getCustomer")
	public List<User> findAllCustomer() {

		return userService.findAllCustomer();
	}

	@GetMapping("getUserById/{userId}")
	public Response<User> getClient(@PathVariable int userId) {

		User theUser = userService.findById(userId);

		if (theUser != null) {
			return new Response<>(false, "records found", theUser);
		} else {
			throw new UserNotFoundException("record not found");
		}

	}

	@PostMapping("/addUser")
	public Response<User> addClient(@Valid @RequestBody User theUser) {

		User res = userService.findByEmail(theUser.getEmail());
		User  res1 = userService.findByPhone(theUser.getMobileNo());
		User res2 = userService.findByAadhar(theUser.getAdharNo());
		

		if( res !=null) {
			return new Response<User>(true,"This Email already Exist",null);
			
		}else if( res1 !=null) {
			return new Response<User>(true,"This Phone Number already Exist",null);

		}else if( res2 !=null){
			return new Response<User>(true,"This Aadhar Number already Exist",null);

		}

		User user1 = userService.save(theUser);

		if (user1 != null) {

			return new Response<>(false, "User added successfully", user1);

		} else {
			return new Response<>(true, "save failed", null);
		}
	}

	@DeleteMapping("/deleteUser/{userId}")
	public Response<User> deleteClient(@PathVariable int userId) {

		User tempUser = userService.findById(userId);

		if (tempUser != null) {
			userService.deleteById(userId);
			return new Response<>(false, "User Deleted Successfully", tempUser);
		} else {
			throw new UserNotFoundException("record not found");
		}
	}

	@GetMapping("/getUserByPage/{pageNo}/{itemsPerPage}")
	public Page<User> getClients(@PathVariable int pageNo, @PathVariable int itemsPerPage) {
		return userService.getClients(pageNo, itemsPerPage);
	}

	@GetMapping("/sortUser/{pageNo}/{itemsPerPage}/{fieldName}")
	public Page<User> getSortClients(@PathVariable int pageNo, @PathVariable int itemsPerPage,
			@PathVariable String fieldName) {
		return userService.getSortClients(pageNo, itemsPerPage, fieldName);
	}

	@GetMapping("/customers/application/{email}")
	public Response<User> getCustomerApplications(@PathVariable String email) {
		User user = userService.getByEmail(email);
		if (userService.getByEmail(email) == null) {
			throw new UserNotFoundException("User not found!!!");
		} else {
			return new Response<User>(true, "Applications Found", user);
		}
	}

	@PostMapping("/makeloan/{email}")
	public Response<Applicant> makeLoan(@PathVariable String email, @Valid @RequestBody Applicant applyloan) {
		User user = userService.getByEmail(email);
		applyloan.setUser(user);
		applicantService.saveApplicant(applyloan);

		if (user == null) {
			throw new UserNotFoundException("User not found!!!");
		} else {
			return new Response<Applicant>(false, "Application saved", applyloan);
		}
	}
	
	@GetMapping("/getRequested/{pageNo}/{itemsPerPage}")
	public Page<User> getAllRequested(@PathVariable int pageNo ,@PathVariable int itemsPerPage){ 
		return userService.getAllRequested(pageNo, itemsPerPage);
	}
	
	@GetMapping("/getSortRequested/{pageNo}/{itemsPerPage}/{fieldName}")
	public Page<User> getAllSortRequested(@PathVariable int pageNo,@PathVariable int itemsPerPage,@PathVariable String fieldName) {
		return userService.getAllSortedRequested(pageNo, itemsPerPage, fieldName);
	}

	@GetMapping("/getCustomer/{pageNo}/{itemsPerPage}")
	public Page<User> getAllCustomer(@PathVariable int pageNo ,@PathVariable int itemsPerPage){ 
		return userService.findAllCustomer(pageNo, itemsPerPage);
	}
	
	@GetMapping("/getSortCustomer/{pageNo}/{itemsPerPage}/{fieldName}")
	public Page<User> getAllSortCustomer(@PathVariable int pageNo,@PathVariable int itemsPerPage,@PathVariable String fieldName) {
		return userService.findAllSortedCustomer(pageNo, itemsPerPage, fieldName);
	}


}
