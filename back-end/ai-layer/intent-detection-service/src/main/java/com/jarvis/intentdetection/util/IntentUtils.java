package com.jarvis.intentdetection.util;

import java.util.Map;

public class IntentUtils {

  private static final Map<String, String> aliases =
      Map.ofEntries(
          Map.entry("ship", "CreateShipment"),
          Map.entry("send", "CreateShipment"),
          Map.entry("track", "TrackShipment"),
          Map.entry("status", "TrackShipment"),
          Map.entry("return", "ReturnShipment"),
          Map.entry("reschedule", "RescheduleDelivery"),
          Map.entry("issue", "ReportIssue"),
          Map.entry("yes", "ConfirmIntent"),
          Map.entry("no", "CancelIntent"));

  public static String normalize(String intent) {
    if (intent == null || intent.isBlank()) return "Unknown";
    intent = intent.trim().replaceAll("[^a-zA-Z]", "");
    return aliases.getOrDefault(intent.toLowerCase(), capitalize(intent));
  }

  private static String capitalize(String str) {
    return str.isEmpty()
        ? "Unknown"
        : str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }
}
