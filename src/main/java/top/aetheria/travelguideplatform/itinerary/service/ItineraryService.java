package top.aetheria.travelguideplatform.itinerary.service;

import top.aetheria.travelguideplatform.common.vo.PageResult;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryCreateDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryInfoDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryListDTO;
import top.aetheria.travelguideplatform.itinerary.dto.ItineraryUpdateDTO;
import top.aetheria.travelguideplatform.itinerary.entity.Itinerary;

import java.util.List;

public interface ItineraryService {
    Itinerary create(Long userId, ItineraryCreateDTO itineraryCreateDTO);

    ItineraryInfoDTO getById(Long id);

    void update(ItineraryUpdateDTO itineraryUpdateDTO,Long userId);

    void delete(Long id,Long userId);

    PageResult<Itinerary> list(ItineraryListDTO itineraryListDTO);
}