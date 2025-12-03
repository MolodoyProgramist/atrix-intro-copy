package com.appsella.atrix.dto.request;

import lombok.Data;

@Data
public class QuizStartRequest {
    private String utmSource;
    private String utmCampaign;
    private String utmMedium;
    private String utmContent;
    private String utmTerm;
    private String adId;
    private String adName;
    private String adPlacement;
    private String adSubnetwork;
    private String adsetId;
    private String adsetName;
    private String campaignId;
    private String campaignName;
    private String creativeTopic;
    private String fbclid;
    private String idfm;
    private String mode;
}
