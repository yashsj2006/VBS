package com.vbs.demo.controller;

import com.vbs.demo.dto.DisplayDto;
import com.vbs.demo.dto.LoginDto;
import com.vbs.demo.dto.UpdateDto;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserRepo userRepo;
    @PostMapping("/register")
    public String register(@RequestBody User user)
    {
        userRepo.save(user);
        return "Signup Successful";
    }
    @PostMapping("/login")
    public String Login(@RequestBody LoginDto u)
    {
     User user=userRepo.findByUsername(u.getUsername());
     if(user==null)
     {
         return "User Not Found";
     }
     if(!u.getPassword().equals(user.getPassword()))
     {
         return "Password Incorrect";
     }
     if(!u.getRole().equals(user.getRole()))
     {
         return "Role incorrect";
     }
     return String.valueOf(user.getId());
    }
    @GetMapping("/get-details/{id}")
    public DisplayDto display(@PathVariable int id)
    {
        User user=userRepo.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        DisplayDto displayDto=new DisplayDto();
        displayDto.setUsername(user.getUsername());
        displayDto.setBalance(user.getBalance());
        return displayDto;
    }
    @PostMapping("/update")
    public String update(@RequestBody UpdateDto obj)
    {
        User user=userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Not found"));
        if(obj.getKey().equalsIgnoreCase("name"))
        {
            if(user.getName().equals(obj.getValue())) return "Cannot Be Same";
            user.setName(obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("password")) {
            if(user.getPassword().equals(obj.getValue())) return "Cannot Be Same";
            user.setPassword(obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("email")) {
            if(user.getEmail().equals(obj.getValue())) return "Cannot Be Same";
            User user2=userRepo.findByEmail(obj.getValue());
            if(user2!=null) return "Email Already Exists";
            user.setPassword(obj.getValue());
        }
        else
        {
            return "Invalid Key";
        }
        userRepo.save(user);
        return "Updated Successfully";
    }
    @PostMapping("/add")
    public String add(@RequestBody User user)
    {
        userRepo.save(user);
        return "Added Successfully";
    }
    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam String sortBy,@RequestParam String order)
    {
        Sort sort;
        if(order.equalsIgnoreCase("desc"))
        {
            sort=Sort.by(sortBy).descending();
        }
        else
        {
            sort=Sort.by(sortBy).ascending();
        }
        return userRepo.findAllByRole("customer",sort);
    }
    @GetMapping("/users/{keyword}")
    public List<User> getUsers(@PathVariable String keyword)
    {
       return userRepo.findByUsernameContainingIgnoreCase(keyword);
    }

}
