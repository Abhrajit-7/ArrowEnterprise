package com.arrow.Arrow.controller;

import com.arrow.Arrow.ExceptionHandling.UserNotFoundException;
import com.arrow.Arrow.dto.WinnerDTO;
import com.arrow.Arrow.dto.BetsDTO;
import com.arrow.Arrow.repository.UserRepository;
import com.arrow.Arrow.services.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/teer")
public class BetController {

    private String messageStorageSlot1;
    private String messageStorageSlot2;

    Logger logger= LoggerFactory.getLogger(BetController.class);

    @Autowired
    private BetService betService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/submit/slot1/{username}")
    public List<String> submitBetSlot1(@RequestBody BetsDTO betsDTO, @PathVariable String username) {
        logger.info("Request JSON: {}", betsDTO);
        // Delegate the request to the service layer
        return betService.submitBet(betsDTO, username);
    }
    @PostMapping("/submit/slot2/{username}")
    public List<String> submitBetSlot2(@RequestBody BetsDTO betsDTO, @PathVariable String username) {
        logger.info("Request JSON: {}", betsDTO);
        // Delegate the request to the service layer
        return betService.submitBet(betsDTO, username);
    }

    @PostMapping("/winner")
    //To be used by admin
    public List<WinnerDTO> getUsernameBySelectedNumbers(@RequestParam("winner") String number, @RequestParam("slot") String slots) {
        return betService.getUsernameBySelectedNumbers(number,slots);
    }

    @GetMapping("/lists/{username}")
    public List<List<String>> getList(@PathVariable String username, @RequestParam("slot") String slot){
        logger.info("Inside getList controller method");
        if(userRepository.findByUsername(username)!=null) {
            logger.info("Username validated");
            return betService.getLatestRecords(username, slot);
        }else {
            throw new UserNotFoundException("User not found");
        }
    }


    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    // Mapped as /app/application
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public String send(String message) throws Exception {
        messageStorageSlot1=message;
        return message;
    }
    @MessageMapping("/applicationSlot2")
    @SendTo("/all/slot2")
    public String sendForSlot2(String slot2Message) throws Exception{
        messageStorageSlot2=slot2Message;
        return slot2Message;
    }
    @GetMapping("/last-message/slot1")
    public String fetchSlot1(){
        return messageStorageSlot1;
    }
    @GetMapping("/last-message/slot2")
    public String fetchSlot2(){
        return messageStorageSlot2;
    }
}
