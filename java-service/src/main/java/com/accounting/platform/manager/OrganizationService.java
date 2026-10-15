package com.accounting.platform.manager;

import com.accounting.platform.common.AlreadyExistsException;
import com.accounting.platform.entity.Organization;
import com.accounting.platform.manager.dto.CreateOrganizationRequest;
import com.accounting.platform.manager.dto.OrganizationResponse;
import com.accounting.platform.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public OrganizationResponse create(CreateOrganizationRequest request) {
        if (organizationRepository.findByInn(request.inn()).isPresent()) {
            throw new AlreadyExistsException("Организация с ИНН " + request.inn() + " уже существует");
        }

        Organization org = new Organization();
        org.setName(request.name());
        org.setInn(request.inn());
        org.setOgrn(request.ogrn());
        org.setAddress(request.address());
        org.setCreatedAt(LocalDateTime.now());

        Organization saved = organizationRepository.save(org);
        return OrganizationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> listAll() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponse::from)
                .toList();
    }
}
