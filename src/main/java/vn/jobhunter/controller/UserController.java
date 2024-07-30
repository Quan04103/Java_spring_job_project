package vn.jobhunter.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.jobhunter.domain.User;
import vn.jobhunter.domain.response.ResCreateUserDTO;
import vn.jobhunter.domain.response.ResUpdateUserDTO;
import vn.jobhunter.domain.response.ResUserDTO;
import vn.jobhunter.domain.response.ResultPaginationDTO;
import vn.jobhunter.service.UserService;
import vn.jobhunter.util.annotation.ApiMessage;
import vn.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + postManUser.getEmail() + "Email đã được sử dụng");
        }
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User abcUser = this.userService.handleCreateUser(postManUser);
        ResCreateUserDTO userDTO = this.userService.convertToResCreateUserDTO(abcUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {

        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với Id " + id + " không tồn tại");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        boolean isIdExist = this.userService.isIdExist(id);
        if (!isIdExist) {
            throw new IdInvalidException("Id không tồn tại");
        }
        User user = this.userService.fetchUserById(id);
        ResUserDTO res = this.userService.convertToResUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        ResultPaginationDTO rs = this.userService.fetchAllUser(spec, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        boolean isIdExist = this.userService.isIdExist(user.getId());
        if (!isIdExist) {
            throw new IdInvalidException("User với Id " + user.getId() + " không tồn tại");
        }
        User tempUser = this.userService.handleUpdateUser(user);
        ResUpdateUserDTO res = this.userService.convertToResUpdateUserDTO(tempUser);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
