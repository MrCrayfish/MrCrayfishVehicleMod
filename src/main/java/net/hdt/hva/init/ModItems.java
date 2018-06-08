package net.hdt.hva.init;

import net.hdt.hva.entity.vehicle.EntityC62SteamLocomotive;
import net.hdt.hva.entity.vehicle.EntityC62SteamLocomotiveTender;
import net.hdt.hva.entity.vehicle.EntityDBIceMotorcart;
import net.hdt.hva.entity.vehicle.EntityRbhTE22;
import net.hdt.hva.enums.TrainEngineTypes;
import net.hdt.hva.enums.TrainWheelTypes;
import net.hdt.hva.items.*;
import net.minecraft.item.Item;

public class ModItems {

    public static final Item SUBMARINE_BODY;
    public static final Item PLANE_BODY, PLANE_WING, PLANE_MOTOR;
    public static final Item HELICOPTER_BODY, HELICOPTER_ROTOR, HELICOPTER_WINGS;
    public static final Item UFO_BODY;
    public static final Item ROCKET_BODY, ROCKET_ENGINES;
    public static final Item CAR_TRAILER_FRONT_HOUSE_BODY, CAR_TRAILER_CONTAINER_BODY;
    public static final Item CONTAINER;
    public static final Item RACE_CAR_BODY;
    public static final Item STEERING_WHEEL;
    public static final Item CAR_WHEEL;

    public static final Item BMX_BIKE_BODY, BMX_BIKE_HANDLE_BAR;
    public static final Item SCOOTER_BODY, SCOOTER_HANDLE_BAR;
    public static final Item MOTORCYCLE_BODY, MOTORCYCLE_HANDLE_BAR;

    public static final Item SNOW_MOBILE_BODY, SNOW_MOBILE_HANDLE_BAR, SNOW_MOBILE_SKI, SNOW_MOBILE_TRACKS_MODULE;

    public static final Item SLEIGHT_BODY, SANTA_SLEIGHT_BODY;

    public static final Item HIGH_BOOSTER_BOARD;
    public static final Item COVER_F_BODY, COVER_F_STEERING_THING;
    public static final Item COVER_P_BODY, COVER_P_STEERING_THING;
    public static final Item COVER_S_BODY, COVER_S_STEERING_THING;

    public static final Item TOY_STEAM_LOCOMOTIVE_BODY, TOY_TRAIN_CARRIAGE_BODY;

    public static final Item[] TRAIN_WHEEL = new Item[3];
    public static final Item[] ENGINE = new Item[4];
    public static final Item DB_ICE_MOTORCART_BODY, C62_STEAM_LOCOMOTIVE_BODY, C62_STEAM_LOCOMOTIVE_TENDER_BODY, RBH_TE_2_2_BODY;
    public static final Item DB_ICE_MOTORCART, C62_STEAM_LOCOMOTIVE, C62_STEAM_LOCOMOTIVE_TENDER, RBH_TE_2_2;

    public static final Item TRAIN_CONTROLLER;

    static {
        SUBMARINE_BODY = new ItemColoredPart("submarine_body");
        PLANE_BODY = new ItemColoredPart("plane_body");
        PLANE_MOTOR = new ItemPart("plane_engine");
        PLANE_WING = new ItemColoredPart("plane_wing");
        HELICOPTER_BODY = new ItemColoredPart("helicopter_body");
        HELICOPTER_ROTOR = new ItemPart("helicopter_rotor");
        HELICOPTER_WINGS = new ItemPart("helicopter_rotor_wings");
        UFO_BODY = new ItemColoredPart("ufo_body");
        ROCKET_BODY = new ItemColoredPart("rocket_body");
        ROCKET_ENGINES = new ItemPart("rocket_engine");
        CAR_TRAILER_FRONT_HOUSE_BODY = new ItemColoredPart("car_trailer_front_house_body");
        CAR_TRAILER_CONTAINER_BODY = new ItemColoredPart("car_trailer_container_holder");
        CONTAINER = new ItemColoredPart("container");
        RACE_CAR_BODY = new ItemColoredPart("racing_car_body");
        STEERING_WHEEL = new ItemColoredPart("steering_wheel");
        CAR_WHEEL = new ItemPart("car_wheel");

        BMX_BIKE_BODY = new ItemColoredPart("bmx_bike_body");
        BMX_BIKE_HANDLE_BAR = new ItemColoredPart("bmx_bike_handle_bar");
        SCOOTER_BODY = new ItemColoredPart("scooter_body");
        SCOOTER_HANDLE_BAR = new ItemColoredPart("scooter_handle_bar");
        MOTORCYCLE_BODY = new ItemColoredPart("motorcycle_body");
        MOTORCYCLE_HANDLE_BAR = new ItemColoredPart("motorcycle_handle_bar");

        SNOW_MOBILE_BODY = new ItemColoredPart("snow_mobile");
        SNOW_MOBILE_HANDLE_BAR = new ItemColoredPart("snow_mobile_handle_bar");
        SNOW_MOBILE_SKI = new ItemPart("snow_mobile_ski");
        SNOW_MOBILE_TRACKS_MODULE = new ItemPart("snow_mobile_tracks_module");

        SLEIGHT_BODY = new ItemPart("sleight");
        SANTA_SLEIGHT_BODY = new ItemColoredPart("santa_sleight");

        HIGH_BOOSTER_BOARD = new ItemColoredPart("high_booster_board");

        COVER_F_BODY = new ItemColoredPart("cover_f_body");
        COVER_F_STEERING_THING = new ItemColoredPart("cover_f_steering_thing");
        COVER_P_BODY = new ItemColoredPart("cover_p_body");
        COVER_P_STEERING_THING = new ItemColoredPart("cover_p_steering_thing");
        COVER_S_BODY = new ItemColoredPart("cover_s_body");
        COVER_S_STEERING_THING = new ItemColoredPart("cover_s_steering_thing");

        TOY_STEAM_LOCOMOTIVE_BODY = new ItemColoredPart("toy_steam_locomotive_body");
        TOY_TRAIN_CARRIAGE_BODY = new ItemColoredPart("toy_train_carriage_body");

        for(TrainWheelTypes wheelTypes : TrainWheelTypes.values()) {
            TRAIN_WHEEL[wheelTypes.getId()] = new ItemPart(wheelTypes.getName() + "_wheel");
        }
        DB_ICE_MOTORCART_BODY = new ItemColoredPart("db_ice_motorcart_electric_body");
        C62_STEAM_LOCOMOTIVE_BODY = new ItemColoredPart("C62_steam_locomotive_body");
        C62_STEAM_LOCOMOTIVE_TENDER_BODY = new ItemColoredPart("C62_steam_locomotive_tender_body");
        RBH_TE_2_2_BODY = new ItemColoredPart("rhb_te_2_2_body");
        DB_ICE_MOTORCART = new ItemModelTrain("db_ice_motorcart_electric", EntityDBIceMotorcart.class);
        RBH_TE_2_2 = new ItemModelTrain("rhb_te_2_2", EntityRbhTE22.class);
        C62_STEAM_LOCOMOTIVE = new ItemModelTrain("C62_steam_locomotive", EntityC62SteamLocomotive.class);
        C62_STEAM_LOCOMOTIVE_TENDER = new ItemModelTrain("C62_steam_locomotive_tender", EntityC62SteamLocomotiveTender.class);
        TRAIN_CONTROLLER = new ItemController();
        for(TrainEngineTypes type : TrainEngineTypes.values()) {
            ENGINE[type.getId()] = new ItemEngine(type);
        }
    }

    public static void register() {
        register(SUBMARINE_BODY);
        register(PLANE_BODY);
        register(PLANE_MOTOR);
        register(PLANE_WING);
        register(HELICOPTER_BODY);
        register(HELICOPTER_ROTOR);
        register(HELICOPTER_WINGS);
        register(UFO_BODY);
        register(ROCKET_BODY);
        register(ROCKET_ENGINES);
        register(CAR_TRAILER_FRONT_HOUSE_BODY);
        register(CAR_TRAILER_CONTAINER_BODY);
        register(CONTAINER);
        register(RACE_CAR_BODY);
        register(STEERING_WHEEL);
        register(CAR_WHEEL);

        register(BMX_BIKE_BODY);
        register(BMX_BIKE_HANDLE_BAR);
        register(SCOOTER_BODY);
        register(SCOOTER_HANDLE_BAR);
        register(MOTORCYCLE_BODY);
        register(MOTORCYCLE_HANDLE_BAR);

        register(SNOW_MOBILE_BODY);
        register(SNOW_MOBILE_HANDLE_BAR);
        register(SNOW_MOBILE_SKI);
        register(SNOW_MOBILE_TRACKS_MODULE);

        register(SLEIGHT_BODY);
        register(SANTA_SLEIGHT_BODY);

        register(HIGH_BOOSTER_BOARD);

        register(COVER_F_BODY);
        register(COVER_F_STEERING_THING);
        register(COVER_P_BODY);
        register(COVER_P_STEERING_THING);
        register(COVER_S_BODY);
        register(COVER_S_STEERING_THING);

        register(TOY_STEAM_LOCOMOTIVE_BODY);
        register(TOY_TRAIN_CARRIAGE_BODY);

        for(TrainWheelTypes type : TrainWheelTypes.values()) {
            register(TRAIN_WHEEL[type.getId()]);
        }

        register(DB_ICE_MOTORCART_BODY);
        register(DB_ICE_MOTORCART);
        register(C62_STEAM_LOCOMOTIVE_BODY);
        register(C62_STEAM_LOCOMOTIVE);
        register(C62_STEAM_LOCOMOTIVE_TENDER_BODY);
        register(C62_STEAM_LOCOMOTIVE_TENDER);
        register(RBH_TE_2_2_BODY);
        register(RBH_TE_2_2);
        register(TRAIN_CONTROLLER);

        for(TrainEngineTypes type : TrainEngineTypes.values()) {
            register(ENGINE[type.getId()]);
        }
    }

    private static void register(Item item) {
        RegistrationHandler.Items.add(item);
    }
}
