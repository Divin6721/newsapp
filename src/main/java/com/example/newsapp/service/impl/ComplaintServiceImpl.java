package com.example.newsapp.service.impl;

import com.example.newsapp.dto.ComplaintAdminResponseRequest;
import com.example.newsapp.dto.ComplaintUserRequest;
import com.example.newsapp.dto.ComplaintDto;
import com.example.newsapp.mapper.ComplaintMapper;
import com.example.newsapp.model.Complaint;
import com.example.newsapp.model.News;
import com.example.newsapp.model.Role;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.ComplaintRepository;
import com.example.newsapp.service.ComplaintService;
import com.example.newsapp.service.PermissionService;
import com.example.newsapp.specification.ComplaintSpecification;
import com.example.newsapp.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final PermissionService permissionService;



    @Override
    @PreAuthorize("hasRole('ADMIN') or #fromUserEmail == authentication.name")
    public Page<ComplaintDto> filterComplaints(String newsTitle, String fromUserEmail, String status,
                                               LocalDate fromDate, LocalDate toDate,
                                               int page, int size,
                                               String sortBy, String direction) {

        Specification<Complaint> spec = Specification.where(null);

        spec = spec.and(ComplaintSpecification.hasNewsTitle(newsTitle));
        spec = spec.and(ComplaintSpecification.hasUserEmail(fromUserEmail));

        Boolean responded = null;
        if ("RESPONDED".equalsIgnoreCase(status)) {
            responded = true;
        } else if ("UNRESOLVED".equalsIgnoreCase(status)) {
            responded = false;
        }

        spec = spec.and(ComplaintSpecification.isResponded(responded));
        spec = spec.and(ComplaintSpecification.createdAfter(fromDate));
        spec = spec.and(ComplaintSpecification.createdBefore(toDate));

        Pageable pageable = PaginationUtils.createPageable(
                page, size, sortBy, direction
        );

        Page<Complaint> complaints = complaintRepository.findAll(spec, pageable);
        return complaints.map(ComplaintMapper::toDto);

    }

    @Override
    @PreAuthorize("hasAnyRole('AUTHOR', 'USER')")
    public ComplaintDto createComplaint(ComplaintUserRequest dto, Authentication authentication) {
        String email = authentication.getName();
        User user = permissionService.checkUserExistsByEmail(email);
        News news = permissionService.checkNewsExists(dto.getNewsId());
        Complaint complaint = ComplaintMapper.toEntity(dto,user,news);
        complaintRepository.save(complaint);
        return  ComplaintMapper.toDto(complaint);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteComplaint(Long complaintId, Authentication authentication) {
      String email = authentication.getName();
      User user =permissionService.checkUserExistsByEmail(email);
      Role role = user.getRole();
      Complaint complaint = permissionService.checkComplaintExists(complaintId);
      permissionService.checkComplaintAccess(user,complaint, role);
      complaintRepository.delete(complaint);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ComplaintDto respondToComplaint(ComplaintAdminResponseRequest dto, Authentication authentication) {
        String email = authentication.getName();
        User user = permissionService.checkUserExistsByEmail(email);
        Role role = user.getRole();
        permissionService.checkAdminAccess(role);

        News news = permissionService.checkNewsExists(dto.getComplaintId());

        Complaint complaint = permissionService.checkComplaintExists(dto.getComplaintId());

        complaint.setResponse(dto.getResponse());
        complaint.setRespondedAt(LocalDateTime.now());

        complaintRepository.save(complaint);
        return ComplaintMapper.toDto(complaint);
    }
}
