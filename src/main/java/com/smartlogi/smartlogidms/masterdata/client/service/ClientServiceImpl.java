package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.RegisterClientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.security.RoleUtils;
import io.github.tawdi.security.permission.repository.RoleRepository;
import io.github.tawdi.security.user.domain.UserAccount;
import io.github.tawdi.security.user.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ClientServiceImpl extends StringCrudServiceImpl<ClientExpediteur, ClientRequestDTO, ClientResponseDTO> implements ClientService {

    private final ClientExpediteurRepository clientExpediteurRepo;
    private final ClientMapper clientMapper;
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientExpediteurRepository clientExpediteurRepository, ClientMapper clientMapper, UserAccountRepository userAccountRepository,RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        super(clientExpediteurRepository, clientMapper);
        this.clientExpediteurRepo = clientExpediteurRepository;
        this.clientMapper = clientMapper;
        this.userAccountRepository =userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository =roleRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO findByEmail(String email) {
        ClientExpediteur entity = clientExpediteurRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with email: " + email));
        return clientMapper.toDto(entity);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> searchClients(String keyword, Pageable pageable) {
        return clientExpediteurRepo.searchClients(keyword, pageable)
                .map(clientMapper::toDto);
    }


    @Override
    @Transactional
    public ClientResponseDTO register(RegisterClientRequestDTO dto) {

        UserAccount user = UserAccount.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(roleRepository.findByName("CLIENT").orElseThrow(()-> new ResourceNotFoundException("Role Not Found")))
                .enabled(true)
                .build();
        userAccountRepository.save(user);

        ClientExpediteur client = clientMapper.toEntity(dto);
        client.setUserAccount(user);

        return clientMapper.toDto(clientExpediteurRepo.save(client));
    }

}
