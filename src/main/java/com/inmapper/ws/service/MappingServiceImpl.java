package com.inmapper.ws.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.inmapper.ws.exception.InvalidMobilePositionException;
import com.inmapper.ws.exception.ResourceNotFoundException;
import com.inmapper.ws.model.domain.RoomMapping;
import com.inmapper.ws.model.domain.UserLocation;
import com.inmapper.ws.model.domain.UserSession;
import com.inmapper.ws.model.to.MobilePositionTo;
import com.inmapper.ws.model.to.RoomMappingTo;
import com.inmapper.ws.model.to.UserPointTo;
import com.inmapper.ws.repository.RoomMappingRepository;

/**
 * Service implementation orchestrating mobile sensor data conversion and persistence.
 * 
 * @author trein
 */
@Service
@Transactional(readOnly = true)
public class MappingServiceImpl implements MappingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingServiceImpl.class);
    
    private final RoomMappingRepository repository;
    private final MappingAlgorithm algorithm;
    
    @Autowired
    public MappingServiceImpl(MappingAlgorithm algorithm, RoomMappingRepository repository) {
        this.algorithm = algorithm;
        this.repository = repository;
    }
    
    @Override
    @Transactional(readOnly = false)
    public String handlePosition(MobilePositionTo position) throws InvalidMobilePositionException {
        UserLocation location = this.algorithm.decodePosition(position);
        RoomMapping mapping = this.repository.findByRoomId(position.getRoomId());
        
        if (mapping == null) {
            mapping = new RoomMapping(position.getRoomId());
        }
        mapping.appendToSession(position.getMobileId(), location);
        
        this.repository.save(mapping);
        
        LOGGER.debug("Saved new location for room {} as {}", position.getRoomId(), location);
        
        return mapping.getRoomId();
    }
    
    @Override
    public RoomMappingTo retrieveRoomLocations(String roomId) throws ResourceNotFoundException {
        RoomMapping mapping = this.repository.findByRoomId(roomId);
        Multimap<String, UserPointTo> mappings = ArrayListMultimap.create();
        
        if (mapping == null) {
            throw new ResourceNotFoundException(roomId);
        }
        
        for (UserSession userSession : mapping.getSessions()) {
            for (UserLocation location : userSession.getLocations()) {
                String mobileId = userSession.getMobileId();
                UserPointTo point = new UserPointTo(roomId, mobileId, location.getX(), location.getY());
                
                mappings.put(mobileId, point);
            }
        }
        
        LOGGER.debug("Found mappings for room {} as {}", roomId, mappings);
        
        return new RoomMappingTo(mappings.asMap());
    }
}
