package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.events.EventMapper;
import ru.practicum.explorewithme.events.EventRepository;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.RequestException;
import ru.practicum.explorewithme.participation.ParticipationRepository;
import ru.practicum.explorewithme.participation.dto.RequestStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compRepository;

    private final EventRepository eventRepository;

    private final ParticipationRepository participationRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        if (compilationDto.getTitle() == null) {
            throw new RequestException("Поле title не должно быть пустым");
        }
        List<EventShortDto> eventsDto = new ArrayList<>();
        List<Event> events = eventRepository.findAllById(compilationDto.getEvents());
        for (Event event : events) {
            eventsDto.add(EventMapper.eventToEventShortDto(event,
                    participationRepository.countByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED)));
        }
        return CompilationMapper.compilationToCompilationDto(compRepository.save(CompilationMapper
                .newCompilationDtoToCompilation(compilationDto, events)), eventsDto);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compRepository.existsById(compId)) {
            throw new NotFoundException("Entity with id-" + compId + " not found");
        }
        compRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public void removeAnEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Entity with id-" + compId + " not found"));
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        List<Event> events = compilation.getEvents().stream()
                .filter(event -> !event.getId().equals(eventId))
                .collect(Collectors.toList());
        compilation.setEvents(events);
        compRepository.save(compilation);
    }

    @Override
    @Transactional
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Entity with id-" + compId + " not found"));
        compilation.getEvents().add(eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено")));
        compRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilation(Long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Entity with id-" + compId + " not found"));
        compilation.setPinned(false);
        compRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilation(Long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Entity with id-" + compId + " not found"));
        compilation.setPinned(true);
        compRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        List<CompilationDto> compDto = new ArrayList<>();
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compRepository.findAll();
            for (Compilation compilation : compilations) {
                List<EventShortDto> eventsDto = new ArrayList<>();
                List<Event> events = compilation.getEvents();
                for (Event event : events) {
                    eventsDto.add(EventMapper.eventToEventShortDto(event,
                            participationRepository.countByEvent_IdAndStatus(event.getId(),
                                    RequestStatus.CONFIRMED)));
                }
                compDto.add(CompilationMapper.compilationToCompilationDto(compilation, eventsDto));
            }
        } else {
            compilations = compRepository.findAllByPinned(pinned, PageRequest.of(from, size)).getContent();
            for (Compilation compilation : compilations) {
                List<EventShortDto> eventsDto = new ArrayList<>();
                List<Event> events = compilation.getEvents();
                for (Event event : events) {
                    eventsDto.add(EventMapper.eventToEventShortDto(event,
                            participationRepository.countByEvent_IdAndStatus(event.getId(),
                                    RequestStatus.CONFIRMED)));
                }
                compDto.add(CompilationMapper.compilationToCompilationDto(compilation, eventsDto));
            }
        }
        return compDto;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Entity with id-" + compId + " not found"));
        List<Event> events = compilation.getEvents();
        List<EventShortDto> eventsDto = new ArrayList<>();
        for (Event event : events) {
            eventsDto.add(EventMapper.eventToEventShortDto(event,
                    participationRepository.countByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED)));
        }
        return CompilationMapper.compilationToCompilationDto(compilation, eventsDto);
    }
}
