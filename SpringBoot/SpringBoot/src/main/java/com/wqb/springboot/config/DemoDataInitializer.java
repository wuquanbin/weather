package com.wqb.springboot.config;

import com.wqb.springboot.entity.*;
import com.wqb.springboot.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final DistrictRepository districtRepository;
    private final WeatherObservationRepository weatherObservationRepository;
    private final WeatherForecastRepository weatherForecastRepository;
    private final TravelSuggestionRepository travelSuggestionRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final RiskSegmentRepository riskSegmentRepository;
    private final WarningNoticeRepository warningNoticeRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final LifeIndexRepository lifeIndexRepository;
    private final SystemParamRepository systemParamRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(
            DistrictRepository districtRepository,
            WeatherObservationRepository weatherObservationRepository,
            WeatherForecastRepository weatherForecastRepository,
            TravelSuggestionRepository travelSuggestionRepository,
            TravelPlaceRepository travelPlaceRepository,
            RiskSegmentRepository riskSegmentRepository,
            WarningNoticeRepository warningNoticeRepository,
            KnowledgeDocumentRepository knowledgeDocumentRepository,
            LifeIndexRepository lifeIndexRepository,
            SystemParamRepository systemParamRepository,
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.districtRepository = districtRepository;
        this.weatherObservationRepository = weatherObservationRepository;
        this.weatherForecastRepository = weatherForecastRepository;
        this.travelSuggestionRepository = travelSuggestionRepository;
        this.travelPlaceRepository = travelPlaceRepository;
        this.riskSegmentRepository = riskSegmentRepository;
        this.warningNoticeRepository = warningNoticeRepository;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
        this.lifeIndexRepository = lifeIndexRepository;
        this.systemParamRepository = systemParamRepository;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (districtRepository.count() > 0) {
            return;
        }

        seedAdminUser();
        seedDistricts();
        seedWeatherData();
        seedForecasts();
        seedTravelSuggestions();
        seedTravelPlaces();
        seedRiskSegments();
        seedWarnings();
        seedLifeIndex();
        seedKnowledgeDocuments();
        seedSystemParams();
    }

    private void seedAdminUser() {
        if (!adminUserRepository.existsByUsername("admin")) {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("超级管理员");
            admin.setRole("SUPER_ADMIN");
            admin.setStatus(1);
            admin.setEmail("admin@foshanweather.com");
            adminUserRepository.save(admin);
        }
    }

    private void seedDistricts() {
        districtRepository.save(new District("chancheng", "禅城区",
                "禅城区全域及下辖镇街", "祖庙/岭南天地/佛山乐园", "地铁广佛线/公交密集"));
        districtRepository.save(new District("nanhai", "南海区",
                "南海区全域及下辖镇街", "千灯湖/西樵山/南国桃园", "广佛高速/佛山一环"));
        districtRepository.save(new District("shunde", "顺德区",
                "顺德区全域及下辖镇街", "清晖园/顺峰山/华侨城欢乐海岸", "广珠城际/佛山地铁3号线"));
        districtRepository.save(new District("gaoming", "高明区",
                "高明区全域及下辖镇街", "皂幕山/盈香生态园", "广明高速"));
        districtRepository.save(new District("sanshui", "三水区",
                "三水区全域及下辖镇街", "三水芦苞祖庙/南丹山", "广三高速"));

        List<District> districts = districtRepository.findAllByOrderByIdAsc();
        double[][] coords = {
                {23.0215, 113.1215},
                {23.0318, 113.1430},
                {22.8057, 113.2543},
                {22.9003, 112.8926},
                {23.1556, 112.8970}
        };
        String[] adminCodes = {"440604", "440605", "440606", "440608", "440607"};
        for (int i = 0; i < districts.size() && i < coords.length; i++) {
            districts.get(i).setLatitude(coords[i][0]);
            districts.get(i).setLongitude(coords[i][1]);
            districts.get(i).setAdminCode(adminCodes[i]);
            districts.get(i).setParentRegion("广东省佛山市");
        }
        districtRepository.saveAll(districts);
    }

    private void seedWeatherData() {
        LocalDateTime now = LocalDateTime.now();
        List<District> districts = districtRepository.findAllByOrderByIdAsc();

        String[] weatherTypes = {"多云", "晴", "阴", "阵雨", "多云"};
        BigDecimal[] temps = {new BigDecimal("33.2"), new BigDecimal("34.1"),
                new BigDecimal("32.5"), new BigDecimal("31.8"), new BigDecimal("33.6")};
        BigDecimal[] apparentTemps = {new BigDecimal("36.5"), new BigDecimal("37.8"),
                new BigDecimal("35.0"), new BigDecimal("34.2"), new BigDecimal("36.9")};
        int[] humiditys = {65, 60, 72, 78, 63};

        for (int i = 0; i < districts.size(); i++) {
            int idx = i % weatherTypes.length;
            WeatherObservation obs = new WeatherObservation(
                    districts.get(i),
                    now.minusMinutes(30),
                    weatherTypes[idx],
                    temps[idx],
                    apparentTemps[idx],
                    humiditys[idx],
                    "东南风",
                    "3级",
                    "良",
                    idx == 3 ? 65 : 20,
                    idx == 3 ? "闷热" : "舒适",
                    idx == 0 ? "中等" : "弱",
                    idx == 3 ? "一般" : "适宜"
            );
            obs.setPressure(new BigDecimal("1013.2"));
            obs.setVisibility(new BigDecimal("15.0"));
            weatherObservationRepository.save(obs);
        }
    }

    private void seedForecasts() {
        LocalDate today = LocalDate.now();
        List<District> districts = districtRepository.findAllByOrderByIdAsc();

        String[] weekDays = new String[3];
        for (int d = 0; d < 3; d++) {
            weekDays[d] = today.plusDays(d).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.CHINESE);
        }

        String[][] weatherTypes = {
                {"多云", "阵雨", "雷阵雨"},
                {"晴", "多云", "阴"},
                {"阵雨", "中雨", "多云"},
                {"阴", "小雨", "多云"},
                {"多云", "晴", "阵雨"}
        };
        BigDecimal[][] lows = {
                {new BigDecimal("26.0"), new BigDecimal("25.5"), new BigDecimal("25.0")},
                {new BigDecimal("27.0"), new BigDecimal("26.5"), new BigDecimal("26.0")},
                {new BigDecimal("25.5"), new BigDecimal("25.0"), new BigDecimal("25.5")},
                {new BigDecimal("25.0"), new BigDecimal("24.5"), new BigDecimal("25.0")},
                {new BigDecimal("26.5"), new BigDecimal("26.0"), new BigDecimal("25.5")}
        };
        BigDecimal[][] highs = {
                {new BigDecimal("34.0"), new BigDecimal("32.0"), new BigDecimal("33.0")},
                {new BigDecimal("35.0"), new BigDecimal("34.5"), new BigDecimal("33.5")},
                {new BigDecimal("33.0"), new BigDecimal("31.0"), new BigDecimal("32.5")},
                {new BigDecimal("32.0"), new BigDecimal("30.0"), new BigDecimal("33.0")},
                {new BigDecimal("34.5"), new BigDecimal("35.0"), new BigDecimal("32.0")}
        };
        int[][] precipProbs = {
                {30, 60, 55},
                {10, 25, 35},
                {55, 75, 40},
                {40, 65, 30},
                {25, 10, 50}
        };

        for (int i = 0; i < districts.size(); i++) {
            int idx = i % weatherTypes.length;
            for (int d = 0; d < 3; d++) {
                WeatherForecast forecast = new WeatherForecast(
                        districts.get(i),
                        today.plusDays(d),
                        weekDays[d],
                        weatherTypes[idx][d],
                        lows[idx][d],
                        highs[idx][d],
                        precipProbs[idx][d],
                        d == 2 ? "西北风" : "东南风",
                        d == 2 ? "4级" : "3级",
                        precipProbs[idx][d] > 50
                                ? "降水概率较高，建议携带雨具，优先选择地铁/公交出行"
                                : "天气较好，适宜步行/骑行等绿色出行方式"
                );
                weatherForecastRepository.save(forecast);
            }
        }
    }

    private void seedTravelSuggestions() {
        List<District> districts = districtRepository.findAllByOrderByIdAsc();
        String[][] suggestions = {
                {"commute", "通勤出行建议", "今日有阵雨可能，早晚高峰路面湿滑风险上升",
                        "建议优先乘坐地铁广佛线或公交出行，驾车请注意佛山大道/季华路易积水路段", "建议优先", "commute", "1"},
                {"outing", "户外出游建议", "午后气温较高，紫外线中等偏强",
                        "推荐千灯湖/顺峰山等有林荫遮蔽的景点，携带防晒用品和充足饮水", "适合出行", "outing", "2"},
                {"family", "亲子活动建议", "下午可能有雷阵雨，不建议长时间户外活动",
                        "推荐岭南天地/祖庙博物馆等室内文化场所，或佛山科学馆", "室内优先", "family", "3"},
                {"cycling", "骑行出行建议", "当前风力适中，能见度良好",
                        "推荐佛山绿道/东平河沿线骑行，避开施工路段，携带雨具备用", "适宜骑行", "cycling", "4"}
        };

        for (District district : districts) {
            for (String[] s : suggestions) {
                TravelSuggestion ts = new TravelSuggestion(
                        district, s[0], s[1], s[2], s[3], s[4], s[5], Integer.parseInt(s[6])
                );
                travelSuggestionRepository.save(ts);
            }
        }
    }

    private void seedTravelPlaces() {
        List<District> districts = districtRepository.findAllByOrderByIdAsc();

        Object[][] places = {
                {"chancheng", "祖庙博物馆", "文化景点", "佛山市禅城区祖庙路21号", "113.1215,23.0290",
                        true, "晴天,阴天", "文化,历史,亲子", 5, "千年古庙，了解佛山武术与粤剧文化"},
                {"chancheng", "岭南天地", "商业街区", "佛山市禅城区天地路", "113.1198,23.0265",
                        true, "雨天,晴天", "美食,购物,休闲", 5, "岭南风格商业街区，室内外结合"},
                {"chancheng", "季华公园", "公园绿地", "佛山市禅城区季华五路", "113.1310,23.0180",
                        false, "晴天", "散步,亲子", 3, "市中心绿地，适合短途休闲"},
                {"nanhai", "千灯湖公园", "公园绿地", "佛山市南海区桂城街道", "113.1550,23.0430",
                        false, "晴天,多云", "散步,骑行,亲子", 5, "佛山地标湖泊公园，夜景绝美"},
                {"nanhai", "西樵山风景名胜区", "自然景区", "佛山市南海区西樵镇", "112.9580,22.9280",
                        false, "晴天", "登山,户外,亲子", 5, "国家5A景区，适合晴天登山观景"},
                {"nanhai", "南海万达广场", "商业综合体", "佛山市南海区桂城街道", "113.1480,23.0380",
                        true, "雨天,高温", "购物,美食,电影", 4, "大型商业综合体，雨天备选"},
                {"shunde", "清晖园", "文化景点", "佛山市顺德区大良街道清晖路", "113.2610,22.8060",
                        false, "晴天,阴天", "文化,历史,拍照", 5, "岭南四大名园之一"},
                {"shunde", "华侨城欢乐海岸PLUS", "主题乐园", "佛山市顺德区大良街道", "113.2700,22.8120",
                        false, "晴天", "游乐,亲子,美食", 5, "大型主题乐园与美食集聚地"},
                {"shunde", "顺峰山公园", "公园绿地", "佛山市顺德区大良街道", "113.2580,22.8150",
                        false, "晴天,多云", "骑行,散步,亲子", 4, "顺德最大公园，环湖绿道"},
                {"gaoming", "皂幕山旅游风景区", "自然景区", "佛山市高明区杨和镇", "112.8650,22.8890",
                        false, "晴天", "登山,户外", 4, "佛山第一峰，适合晴天远足"},
                {"gaoming", "盈香生态园", "生态农庄", "佛山市高明区荷城街道", "112.8800,22.8960",
                        false, "晴天,多云", "亲子,采摘,生态", 4, "生态农业体验园"},
                {"sanshui", "芦苞祖庙", "文化景点", "佛山市三水区芦苞镇", "112.8810,23.1700",
                        false, "晴天,阴天", "文化,历史", 3, "北江边古庙，三水文化地标"},
                {"sanshui", "南丹山森林王国", "自然景区", "佛山市三水区南山镇", "112.8600,23.1400",
                        false, "晴天", "户外,亲子,拓展", 4, "森林探险与户外拓展基地"}
        };

        for (Object[] p : places) {
            String code = (String) p[0];
            District district = districts.stream()
                    .filter(d -> d.getCode().equals(code))
                    .findFirst().orElse(districts.get(0));

            TravelPlace tp = new TravelPlace(
                    district,
                    (String) p[1],
                    (String) p[2],
                    (String) p[3],
                    (String) p[4],
                    (Boolean) p[5],
                    (String) p[6],
                    (String) p[7],
                    (Integer) p[8],
                    (String) p[9]
            );
            travelPlaceRepository.save(tp);
        }
    }

    private void seedRiskSegments() {
        List<District> districts = districtRepository.findAllByOrderByIdAsc();

        Object[][] risks = {
                {"chancheng", "佛山大道-季华路口", "禅城区佛山大道与季华路交汇处",
                        "积水", "暴雨,大雨", "暴雨天气该路口排水不畅，易出现30cm以上积水", "建议绕行岭南大道或走地铁", 1},
                {"chancheng", "汾江路铁路桥底", "禅城区汾江路铁路桥涵洞",
                        "积水", "暴雨,大雨", "地势低洼，暴雨时易严重积水，曾有车辆被困", "暴雨天气切勿强行通过，建议绕行", 1},
                {"nanhai", "桂澜路-海八路立交", "南海区桂澜路与海八路交汇处",
                        "积水", "暴雨", "暴雨时排水压力大，路面湿滑", "注意减速慢行，保持车距", 2},
                {"shunde", "105国道容桂段", "顺德区105国道容桂路段",
                        "拥堵", "暴雨,台风", "台风/暴雨时大型车辆多，路面湿滑易追尾", "建议改走广珠西线高速", 2},
                {"sanshui", "西南街道北江大堤段", "三水区西南街道北江大堤",
                        "积水", "暴雨,台风", "暴雨台风天气北江水位上涨，堤段可能封路", "关注实时路况，听从交警指挥", 1}
        };

        for (Object[] r : risks) {
            String code = (String) r[0];
            District district = districts.stream()
                    .filter(d -> d.getCode().equals(code))
                    .findFirst().orElse(districts.get(0));

            RiskSegment rs = new RiskSegment(
                    district,
                    (String) r[1],
                    (String) r[2],
                    (String) r[3],
                    (String) r[4],
                    (String) r[5],
                    (String) r[6],
                    (Integer) r[7]
            );
            riskSegmentRepository.save(rs);
        }
    }

    private void seedWarnings() {
        LocalDateTime now = LocalDateTime.now();

        WarningNotice w1 = new WarningNotice(
                null, "暴雨", "黄色",
                "佛山市暴雨黄色预警",
                "预计未来6小时内禅城、南海、顺德将出现50毫米以上降水，并伴有雷电和短时大风，请注意防御。",
                now.minusHours(1), now.plusHours(6), "active",
                "禅城区、南海区、顺德区",
                "1. 停止户外作业；2. 转移危险地带人员；3. 驾车注意路面积水，减速慢行"
        );
        warningNoticeRepository.save(w1);

        WarningNotice w2 = new WarningNotice(
                null, "高温", "橙色",
                "佛山市高温橙色预警",
                "预计今日最高气温将达37℃以上，请注意防暑降温。",
                now.minusHours(3), now.plusHours(12), "active",
                "佛山全市",
                "1. 户外作业人员做好防暑措施；2. 老人儿童避免长时间户外活动；3. 注意补充水分"
        );
        warningNoticeRepository.save(w2);

        WarningNotice w3 = new WarningNotice(
                null, "雷雨大风", "蓝色",
                "佛山市雷雨大风蓝色预警",
                "预计未来2小时内部分地区将出现8级左右雷雨大风。",
                now.minusDays(1), now.minusHours(12), "expired",
                "高明区、三水区",
                "1. 关好门窗；2. 远离广告牌和临时搭建物；3. 停止露天集体活动"
        );
        warningNoticeRepository.save(w3);
    }

    private void seedLifeIndex() {
        LocalDate today = LocalDate.now();
        List<District> districts = districtRepository.findAllByOrderByIdAsc();

        Object[][] indices = {
                {"dress", "舒适", "建议穿短袖、短裤等夏季服装", "天气炎热，选择透气性好的衣物"},
                {"uv", "中等", "紫外线强度中等", "外出建议涂抹SPF30+防晒霜"},
                {"carwash", "不宜", "未来有降水，不适宜洗车", "建议雨后再安排洗车"},
                {"sport", "较适宜", "适合进行户外运动", "建议避开午间高温时段"},
                {"sunrise", "适宜", "适合晾晒衣物", "午后可能有阵雨，建议上午晾晒"},
                {"travel", "适宜", "适宜出行旅游", "携带防晒和雨具，做好两手准备"}
        };

        for (District district : districts) {
            for (Object[] idx : indices) {
                LifeIndex li = new LifeIndex();
                li.setDistrict(district);
                li.setIndexDate(today);
                li.setIndexType((String) idx[0]);
                li.setLevel((String) idx[1]);
                li.setDescription((String) idx[2]);
                li.setAdvice((String) idx[3]);
                lifeIndexRepository.save(li);
            }
        }
    }

    private void seedKnowledgeDocuments() {
        knowledgeDocumentRepository.save(new KnowledgeDocument(
                "佛山市暴雨天气出行指南", "出行安全",
                "暴雨天气下佛山市民出行安全指引，涵盖积水路段、避险场所、出行方式选择等",
                "暴雨天气出行注意事项：1. 关注气象预警；2. 避开佛山大道/季华路等易积水路段；3. 优先选择地铁出行；4. 驾车遇积水勿强行通过；5. 远离河道和低洼地带。",
                "暴雨,出行安全,积水,避险", "官方文档", true, LocalDateTime.now().minusDays(3)
        ));
        knowledgeDocumentRepository.save(new KnowledgeDocument(
                "佛山市台风防御手册", "灾害防御",
                "台风来临前、中、后的防御措施和应急指引",
                "台风防御要点：1. 提前储备食物和饮水；2. 加固门窗；3. 收起阳台花盆等物品；4. 避免外出；5. 远离海边和河边；6. 关注官方通报。",
                "台风,防御,应急,安全", "官方文档", true, LocalDateTime.now().minusDays(7)
        ));
        knowledgeDocumentRepository.save(new KnowledgeDocument(
                "佛山五区主要交通干道风险图谱", "交通安全",
                "梳理佛山五区主要干道在不同天气条件下的风险等级和绕行建议",
                "主要风险路段：禅城区佛山大道季华路口（暴雨积水）、南海区桂澜路海八路立交（湿滑）、顺德区105国道容桂段（大风拥堵）、三水区北江大堤段（水位上涨）。",
                "交通,风险路段,积水,拥堵", "运营整理", true, LocalDateTime.now().minusDays(1)
        ));
        knowledgeDocumentRepository.save(new KnowledgeDocument(
                "佛山本地气候特征与季节出行建议", "气候知识",
                "介绍佛山亚热带季风气候特征，各季节天气特点与出行建议",
                "佛山属亚热带季风气候：春季（3-4月）潮湿多雾；夏季（5-9月）高温多雨，台风频发；秋季（10-11月）凉爽干燥；冬季（12-2月）温和少雨。夏季出行需特别关注暴雨和高温预警。",
                "气候,季节,佛山,亚热带", "知识库", true, LocalDateTime.now().minusDays(14)
        ));
    }

    private void seedSystemParams() {
        systemParamRepository.save(createParam("weather.sync.interval", "30", "气象数据同步间隔（分钟）", "weather"));
        systemParamRepository.save(createParam("warning.push.enabled", "true", "预警推送开关", "notification"));
        systemParamRepository.save(createParam("commute.remind.time", "07:30", "通勤提醒推送时间", "notification"));
        systemParamRepository.save(createParam("amap.key", "98fe5dbee13fd24b9f219e2350ffcc77", "高德地图API Key", "amap"));
        systemParamRepository.save(createParam("system.version", "1.0.0", "系统版本号", "system"));
    }

    private SystemParam createParam(String key, String value, String desc, String group) {
        SystemParam param = new SystemParam();
        param.setParamKey(key);
        param.setParamValue(value);
        param.setDescription(desc);
        param.setGroupName(group);
        return param;
    }
}