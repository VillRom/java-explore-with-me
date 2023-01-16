package ru.practicum.explorewithme.statsclient.endpoint;

import lombok.Data;

import java.util.List;

@Data
public class Views {

    private List<ViewsStats> viewsStats;
}
