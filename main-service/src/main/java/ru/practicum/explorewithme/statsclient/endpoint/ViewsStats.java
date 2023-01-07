package ru.practicum.explorewithme.statsclient.endpoint;

import lombok.Data;

@Data
public class ViewsStats {

    private String app;

    private String uri;

    private Long hits;
}
