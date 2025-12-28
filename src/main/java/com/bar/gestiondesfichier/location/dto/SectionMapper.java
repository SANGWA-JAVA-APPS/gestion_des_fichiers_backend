package com.bar.gestiondesfichier.location.dto;

import com.bar.gestiondesfichier.location.model.Section;
import com.bar.gestiondesfichier.location.model.Module;



public class SectionMapper {

    public static Section toEntity(SectionRequestDTO dto, Module module) {
        if (dto == null) return null;

        Section section = new Section();
        section.setName(dto.getName());
        section.setDescription(dto.getDescription());
        section.setSectionCode(dto.getSectionCode());
        section.setFloorNumber(dto.getFloorNumber());
        section.setRoomNumber(dto.getRoomNumber());
        section.setCapacity(dto.getCapacity());
        section.setCoordinates(dto.getCoordinates());
        section.setModule(module);

        if (dto.getSectionType() != null && !dto.getSectionType().isBlank()) {
            try {
                section.setSectionType(Section.SectionType.valueOf(dto.getSectionType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (dto.getAccessLevel() != null && !dto.getAccessLevel().isBlank()) {
            try {
                section.setAccessLevel(Section.AccessLevel.valueOf(dto.getAccessLevel().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        return section;
    }

    public static SectionResponseDTO toResponseDTO(Section section) {
        if (section == null) return null;

        return new SectionResponseDTO(
                section.getId(),
                section.getName(),
                section.getDescription(), // include description
                section.getSectionCode(),
                section.getSectionType() != null ? section.getSectionType().name() : null,
                section.getFloorNumber(),
                section.getRoomNumber(),
                section.getCapacity(),
                section.getCoordinates(),
                section.getAccessLevel() != null ? section.getAccessLevel().name() : null,
                section.getModule() != null ? section.getModule().getId() : null,
                section.getModule() != null ? section.getModule().getName() : null
        );
    }

    public static void updateEntity(SectionRequestDTO dto, Section section, Module module) {
        if (dto == null || section == null) return;

        section.setName(dto.getName());
        section.setDescription(dto.getDescription());
        section.setSectionCode(dto.getSectionCode());
        section.setFloorNumber(dto.getFloorNumber());
        section.setRoomNumber(dto.getRoomNumber());
        section.setCapacity(dto.getCapacity());
        section.setCoordinates(dto.getCoordinates());
        section.setModule(module);

        if (dto.getSectionType() != null && !dto.getSectionType().isBlank()) {
            try {
                section.setSectionType(Section.SectionType.valueOf(dto.getSectionType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (dto.getAccessLevel() != null && !dto.getAccessLevel().isBlank()) {
            try {
                section.setAccessLevel(Section.AccessLevel.valueOf(dto.getAccessLevel().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
