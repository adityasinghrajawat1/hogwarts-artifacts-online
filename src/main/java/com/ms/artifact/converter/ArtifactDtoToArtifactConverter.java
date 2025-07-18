package com.ms.artifact.converter;

import com.ms.artifact.Artifact;
import com.ms.artifact.dto.ArtifactDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact>
{
    @Override
    public Artifact convert(ArtifactDto source) {
        Artifact artifact = new Artifact();
        artifact.setId(source.id());
        artifact.setName(source.name());
        artifact.setDescription(source.description());
        artifact.setImageUrl(source.imageUrl());
        return artifact;
    }
}
