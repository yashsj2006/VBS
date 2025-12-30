package com.vbs.demo.controller;

import com.vbs.demo.dto.TransactionDto;
import com.vbs.demo.dto.TransferDto;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;
    @PostMapping("/deposit")
    public String deposit(@RequestBody TransactionDto obj)
    {
        User user=userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("WrongId"));
        double newBalance=obj.getAmount()+user.getBalance();
        user.setBalance(newBalance);
        userRepo.save(user);
        Transaction t=new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs"+obj.getAmount()+"Deposit Successful");
        t.setUserId(obj.getId());
        transactionRepo.save(t);
        return "Deposit Successful";
    }
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionDto obj)
    {
        User user=userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("WrongId"));
        double newBalance=user.getBalance()-obj.getAmount();
        if(newBalance<0)
        {
            return "Insufficient Balance";
        }
        user.setBalance(newBalance);
        userRepo.save(user);
        Transaction t=new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs"+obj.getAmount()+"Withdrawal Successful");
        t.setUserId(obj.getId());
        transactionRepo.save(t);
        return "Withdrawal Successful";
    }
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferDto obj)
    {
        User sender=userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Id not found"));
        User rec=userRepo.findByUsername(obj.getUsername());
        if(rec==null)
        {
            return "Receiver not found";
        }
        if(sender.getId()==rec.getId())
        {
            return "No self transaction";
        }
        if(obj.getAmount()<1)
        {
            return "Insufficient amount";
        }
        double sbalance= sender.getBalance()- obj.getAmount();
        double rbalance=rec.getBalance()+obj.getAmount();
        sender.setBalance(sbalance);
        rec.setBalance(rbalance);
        userRepo.save(sender);
        userRepo.save(rec);
        Transaction t1=new Transaction();
        Transaction t2=new Transaction();
        t1.setAmount(obj.getAmount());
        t1.setCurrBalance(sbalance);
        t1.setDescription("Rs"+obj.getAmount()+"Sent to user"+obj.getUsername());
        t1.setUserId(obj.getId());
        transactionRepo.save(t1);
        t2.setCurrBalance(rbalance);
        t2.setDescription("Rs"+obj.getAmount()+"Received from sender"+sender.getUsername());
        t2.setUserId(obj.getId());
        transactionRepo.save(t2);
        return "Transfer Successful";
    }
    @GetMapping("/passbook/{id}")
    public List<Transaction> getPassBook(@PathVariable int id)
    {
        return transactionRepo.findAllByUserId(id);
    }
}
