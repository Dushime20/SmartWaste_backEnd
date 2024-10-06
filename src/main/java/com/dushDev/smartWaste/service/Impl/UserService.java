package com.dushDev.smartWaste.service.Impl;

import com.dushDev.smartWaste.Dto.LoginRequest;
import com.dushDev.smartWaste.Dto.Response;
import com.dushDev.smartWaste.Dto.UserDto;
import com.dushDev.smartWaste.entity.User;
import com.dushDev.smartWaste.exception.OurException;
import com.dushDev.smartWaste.repo.UserRepository;
import com.dushDev.smartWaste.service.CloudinaryService;
import com.dushDev.smartWaste.service.Intrefac.IUserService;
import com.dushDev.smartWaste.utils.JWTUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Response register(User user) {

        Response response = new Response();
        try{
            if(userRepository.existsByEmail(user.getEmail())){
                throw new OurException(user.getEmail() + "Already exist");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDto userDto = modelMapper.map(savedUser,UserDto.class);
            response.setStatusCode(200);
            response.setUser(userDto);

        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error Occurred during register a user" + e.getMessage());
        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
         Response response = new Response();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new OurException("user not found"));
            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 days");
            response.setMessage("login successful");

        }catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occurred during login a user");
        }
        return response;

    }

    @Override
    public Response getAllUser() {

        Response response = new Response();
        try{

            List<User> userList = userRepository.findAll();
            Type listType = new TypeToken<List<UserDto>>() {}.getType();
            List<UserDto> userDtoList = modelMapper.map(userList, listType);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserLIST(userDtoList);

        }
        catch (OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error getting all user");
        }
        return response;
    }

    @Override
    public Response resetPassword(Long userId, String email, String password) {
        Response response = new Response();
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

            if (!user.getEmail().equals(email)) {
                throw new OurException("Email not found");
            }

            user.setPassword(passwordEncoder.encode(password)); // Encode the new password

            User resetPassword = userRepository.save(user);
            UserDto userDto = modelMapper.map(resetPassword, UserDto.class);

            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setUser(userDto);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error resetting password: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response addUserProfile(Long userId, MultipartFile photo) {
        Response response = new Response();
        try {
            // Log the received file details
            System.out.println("Received file: " + photo.getOriginalFilename());

            // Save the image to Cloudinary
            String imageUrl = cloudinaryService.saveImageToCloudinary(photo);

            // Log the image URL received from Cloudinary
            System.out.println("Image URL: " + imageUrl);

            // Fetch the user
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

            // Update the user's profile picture
            user.setProfilePicture(imageUrl);

            // Save the updated user
            User savedUser = userRepository.save(user);

            // Map the saved user to a UserDto
            UserDto userDto = modelMapper.map(savedUser, UserDto.class);

            // Set the response details
            response.setStatusCode(200);
            response.setMessage("Profile updated successfully");
            response.setUser(userDto);
        } catch (OurException e) {
            // Handle custom exceptions
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatusCode(500);
            response.setMessage("Error adding user profile: " + e.getMessage());
        }
        return response;
    }



    @Override
    public Response updateUserProfile(Long userId, String name, String phoneNumber, String email, String address, MultipartFile photo) {

        Response response = new Response();
        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = cloudinaryService.saveImageToCloudinary(photo);
            }

            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

            if (name != null && !name.isEmpty()) user.setName(name);
            if (email != null && !email.isEmpty()) user.setEmail(email);
            if (phoneNumber != null && !phoneNumber.isEmpty()) user.setPhoneNumber(phoneNumber);
            if (address != null && !address.isEmpty()) user.setAddress(address);
            if (imageUrl != null) user.setProfilePicture(imageUrl);

            user.setUpdatedAt(LocalDateTime.now());

            User updatedUser = userRepository.save(user);
            UserDto userDto = modelMapper.map(updatedUser, UserDto.class);

            response.setStatusCode(200);
            response.setUser(userDto);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating user profile: " + e.getMessage());
        }

        return response;
    }


    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();
        try {

            userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("user not found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("successful");

        }
        catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error deleting user user");
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();
        try {

            User user=userRepository.findById(Long.valueOf(userId)).orElseThrow(()-> new OurException("user not found"));
            UserDto userDto =modelMapper.map(user,UserDto.class);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDto);

        }
        catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get user by id");
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();
        try {

            User user=userRepository.findByEmail(email).orElseThrow(()-> new OurException("user not found"));
            UserDto userDto =modelMapper.map(user,UserDto.class) ;
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDto);

        }
        catch (OurException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Error get myInfo");
        }
        return response;
    }
}
