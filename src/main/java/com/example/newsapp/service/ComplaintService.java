package com.example.newsapp.service;

import com.example.newsapp.dto.ComplaintAdminResponseRequest;
import com.example.newsapp.dto.ComplaintUserRequest;
import com.example.newsapp.dto.ComplaintDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface ComplaintService {
 Page<ComplaintDto> filterComplaints(String newsTitle, String fromUserEmail, String status,
                                     LocalDate fromDate, LocalDate toDate,
                                     int page, int size,
                                     String sortBy, String direction);
 ComplaintDto createComplaint(ComplaintUserRequest dto, Authentication authentication);
 void deleteComplaint(Long complaintId, Authentication authentication);
 ComplaintDto respondToComplaint(ComplaintAdminResponseRequest dto, Authentication authentication);
}
