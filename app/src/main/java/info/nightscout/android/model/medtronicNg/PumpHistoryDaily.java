package info.nightscout.android.model.medtronicNg;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import info.nightscout.android.R;
import info.nightscout.android.history.HistoryUtils;
import info.nightscout.android.history.MessageItem;
import info.nightscout.android.history.NightscoutItem;
import info.nightscout.android.history.PumpHistoryParser;
import info.nightscout.android.history.PumpHistorySender;
import info.nightscout.android.utils.FormatKit;
import info.nightscout.android.upload.nightscout.TreatmentsEndpoints;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

/**
 * Created by Pogman on 01.04.18.
 */

public class PumpHistoryDaily extends RealmObject implements PumpHistoryInterface {
    @Ignore
    private static final String TAG = PumpHistoryDaily.class.getSimpleName();

    @Index
    private String senderREQ = "";
    @Index
    private String senderACK = "";
    @Index
    private String senderDEL = "";

    @Index
    private Date eventDate;
    @Index
    private long pumpMAC;

    private String key; // unique identifier for nightscout, key = "ID" + RTC as 8 char hex ie. "CGM6A23C5AA"

    @Index
    private int eventRTC;
    private int eventOFFSET;

    @Index
    private int type;

    private Date startDate;
    private Date endDate;
    private int rtc;
    private int offset;
    private int duration;

    private int meterBgCount;
    private int meterBgAverage;
    private int meterBgLow;
    private int meterBgHigh;
    private int manualBgCount;
    private int manualBgAverage;
    private int manualBgLow;
    private int manualBgHigh;
    private int bgCount;
    private int bgAverage;
    private int bgStdDev;

    private double totalInsulin;
    private double basalInsulin;
    private int basalPercent;
    private double bolusInsulin;
    private int bolusPercent;

    private int carbUnits;
    private int bolusWizardUsageCount;
    private int mealWizardUsageCount;

    private double totalFoodInput;
    private double totalBolusWizardInsulinAsFoodOnlyBolus;
    private double totalBolusWizardInsulinAsCorrectionOnlyBolus;
    private double totalBolusWizardInsulinAsFoodAndCorrection;
    private double totalMealWizardInsulinAsFoodOnlyBolus;
    private double totalMealWizardInsulinAsCorrectionOnlyBolus;
    private double totalMealWizardInsulinAsFoodAndCorrection;
    private double totalManualBolusInsulin;
    private int bolusWizardFoodOnlyBolusCount;
    private int bolusWizardCorrectionOnlyBolusCount;
    private int bolusWizardFoodAndCorrectionBolusCount;
    private int mealWizardFoodOnlyBolusCount;
    private int mealWizardCorrectionOnlyBolusCount;
    private int mealWizardFoodAndCorrectionBolusCount;
    private int manualBolusCount;

    private int sgCount;
    private int sgAverage;
    private int sgStddev;
    private int sgDurationAboveHigh;
    private int sgPercentAboveHigh;
    private int sgDurationWithinLimit;
    private int sgPercentWithinLimit;
    private int sgDurationBelowLow;
    private int sgPercentBelowLow;
    private int lgSuspensionDuration;

    private int highPredictiveAlerts;
    private int lowPredictiveAlerts;
    private int lowBgAlerts;
    private int highBgAlerts;
    private int risingRateAlerts;
    private int fallingRateAlerts;
    private int lowGlucoseSuspendAlerts;
    private int predictiveLowGlucoseSuspendAlerts;

    private int microBolusCount;
    private double microBolusInsulinDelivered;
    private int totalTimeInCLActiveMode;
    private int totalTimeInTherapyTargetRange;
    private int totalTimeInAboveTherapyTargetRangeHiLimit;
    private int totalTimeInBelowTherapyTargetRangeLowLimit;

    @Override
    public List<NightscoutItem> nightscout(PumpHistorySender pumpHistorySender, String senderID) {
        List<NightscoutItem> nightscoutItems = new ArrayList<>();

        TreatmentsEndpoints.Treatment treatment = HistoryUtils.nightscoutTreatment(nightscoutItems, this, senderID);
        treatment.setEventType("Pump Daily Totals");

        String notes = "";

        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE MMMM dd", Locale.getDefault());

        long midday = startDate.getTime() + ((endDate.getTime() - startDate.getTime()) / 2);

        if (pumpHistorySender.isOpt(senderID, PumpHistorySender.SENDEROPT.FORMAT_HTML)) {

            String css = ".dt{background-color:#ddd;text-align:center;line-height:1.5;font-weight:bold}" +
                    ".dt caption{background-color:#444;color:#fff}" +
                    ".dt td{background-color:#fff;font-size:83%;min-width:60px}";

            if (TYPE.DAILY_TOTALS.equals(type)) {

                notes += String.format("<style>%s</style><table class='dt'><caption>%s • %s</caption>" +
                                "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr><tr>" +
                                "<td>%s<br>%s<br><br>%s<br>%s<br>%s<br><br>%s<br>%s<br>%s</td>" +
                                "<td>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s</td>" +
                                "<td>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s</td>" +
                                "<td>%s<br>x%s<br>%s<br>%s<br>%s<br>x%s<br>%s<br>%s<br>x%s<br>%s<br>%s<br>x%s<br>%s<br>%s<br>x%s<br>%s</td>" +
                                "<td>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s</td>" +
                                "</tr></table>",

                        css,

                        FormatKit.getInstance().getString(R.string.dt_heading_Daily_Totals),
                        sdfDay.format(midday),

                        FormatKit.getInstance().getString(R.string.dt_heading_Insulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Sensor),
                        FormatKit.getInstance().getString(R.string.dt_heading_BG),
                        FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Alerts),

                        FormatKit.getInstance().getString(R.string.dt_heading_TDD),
                        FormatKit.getInstance().formatAsInsulin(totalInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                        FormatKit.getInstance().formatAsPercent(bolusPercent),
                        FormatKit.getInstance().formatAsInsulin(bolusInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Basal),
                        FormatKit.getInstance().formatAsPercent(basalPercent),
                        FormatKit.getInstance().formatAsInsulin(basalInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Total),
                        sgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(sgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                        FormatKit.getInstance().formatAsGlucose(sgStddev),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsPercent(sgPercentAboveHigh),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationAboveHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Within),
                        FormatKit.getInstance().formatAsPercent(sgPercentWithinLimit),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationWithinLimit),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsPercent(sgPercentBelowLow),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationBelowLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_Total),
                        bgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(bgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsGlucose(meterBgHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsGlucose(meterBgLow),
                        FormatKit.getInstance().getString(R.string.dt_heading_Manual),
                        manualBgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsGlucose(manualBgHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsGlucose(manualBgLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_Wizard),
                        bolusWizardUsageCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Carb),
                        PumpHistoryParser.CARB_UNITS.EXCHANGES.equals(carbUnits) ?
                                FormatKit.getInstance().formatAsExchanges(totalFoodInput) :
                                FormatKit.getInstance().formatAsGrams(totalFoodInput),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food),
                        bolusWizardFoodOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Correct),
                        bolusWizardCorrectionOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsCorrectionOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food_Corr),
                        bolusWizardFoodAndCorrectionBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodAndCorrection),
                        FormatKit.getInstance().getString(R.string.dt_heading_Manual),
                        manualBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalManualBolusInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Total),
                        highPredictiveAlerts + highBgAlerts + lowPredictiveAlerts + lowBgAlerts
                                + predictiveLowGlucoseSuspendAlerts + lowGlucoseSuspendAlerts + risingRateAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_PreHigh),
                        highPredictiveAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        highBgAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_PreLow),
                        lowPredictiveAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        lowBgAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_PreSuspd),
                        predictiveLowGlucoseSuspendAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_Suspend),
                        lowGlucoseSuspendAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_Rise),
                        risingRateAlerts);

            } else {

                notes += String.format("<style>%s</style><table class='dt'><caption>%s • %s</caption>" +
                                "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr><tr>" +
                                "<td>%s<br>%s<br><br>%s<br>%s<br>%s<br><br>%s<br>%s<br>%s</td>" +
                                "<td>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s</td>" +
                                "<td>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s</td>" +
                                "<td>%s<br>x%s<br>%s<br>%s<br>%s<br>x%s<br>%s<br>%s<br>x%s<br>%s<br>%s<br>x%s<br>%s<br>%s<br>x%s<br>%s</td>" +
                                "<td>%s<br>%s<br>%s<br>x%s<br>%s<br><br>%s<br>%s<br>%s<br>%s<br>%s<br>%s<br>%s</td>" +
                                "</tr></table>",

                        css,

                        FormatKit.getInstance().getString(R.string.dt_heading_Daily_Totals),
                        sdfDay.format(midday),

                        FormatKit.getInstance().getString(R.string.dt_heading_Insulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Sensor),
                        FormatKit.getInstance().getString(R.string.dt_heading_BG),
                        FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Auto),

                        FormatKit.getInstance().getString(R.string.dt_heading_TDD),
                        FormatKit.getInstance().formatAsInsulin(totalInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                        FormatKit.getInstance().formatAsPercent(bolusPercent),
                        FormatKit.getInstance().formatAsInsulin(bolusInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Basal),
                        FormatKit.getInstance().formatAsPercent(basalPercent),
                        FormatKit.getInstance().formatAsInsulin(basalInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Total),
                        sgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(sgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                        FormatKit.getInstance().formatAsGlucose(sgStddev),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsPercent(sgPercentAboveHigh),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationAboveHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Within),
                        FormatKit.getInstance().formatAsPercent(sgPercentWithinLimit),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationWithinLimit),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsPercent(sgPercentBelowLow),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationBelowLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_Total),
                        bgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(bgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                        FormatKit.getInstance().formatAsGlucose(bgStdDev),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsGlucose(meterBgHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsGlucose(meterBgLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_Wizard),
                        bolusWizardUsageCount + mealWizardUsageCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Carb),
                        PumpHistoryParser.CARB_UNITS.EXCHANGES.equals(carbUnits) ?
                                FormatKit.getInstance().formatAsExchanges(totalFoodInput) :
                                FormatKit.getInstance().formatAsGrams(totalFoodInput),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food),
                        bolusWizardFoodOnlyBolusCount + mealWizardFoodOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodOnlyBolus + totalMealWizardInsulinAsFoodOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Correct),
                        bolusWizardCorrectionOnlyBolusCount + mealWizardCorrectionOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsCorrectionOnlyBolus + totalMealWizardInsulinAsCorrectionOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food_Corr),
                        bolusWizardFoodAndCorrectionBolusCount + mealWizardFoodAndCorrectionBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodAndCorrection + totalMealWizardInsulinAsFoodAndCorrection),
                        FormatKit.getInstance().getString(R.string.dt_heading_Manual),
                        manualBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalManualBolusInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Active),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInCLActiveMode),
                        FormatKit.getInstance().getString(R.string.dt_heading_Micro),
                        microBolusCount,
                        FormatKit.getInstance().formatAsInsulin(microBolusInsulinDelivered),
                        FormatKit.getInstance().getString(R.string.dt_heading_Therapy),
                        FormatKit.getInstance().getString(R.string.dt_heading_therapy_Range),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInTherapyTargetRange),
                        FormatKit.getInstance().getString(R.string.dt_heading_therapy_Above),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInAboveTherapyTargetRangeHiLimit),
                        FormatKit.getInstance().getString(R.string.dt_heading_therapy_Below),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInBelowTherapyTargetRangeLowLimit));
            }

        } else {

            if (TYPE.DAILY_TOTALS.equals(type)) {

                notes += String.format("%s %s" +
                                " | %s %s %s %s %s %s %s %s" +
                                " | %s x%s %s %s %s %s %s %s %s %s %s %s %s %s %s" +
                                " | %s x%s %s %s %s %s %s %s %s x%s %s %s %s %s" +
                                " | %s x%s %s %s %s x%s %s %s x%s %s %s x%s %s %s x%s %s" +
                                " | %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s",

                        FormatKit.getInstance().getString(R.string.dt_heading_Daily_Totals),
                        sdfDay.format(midday),

                        FormatKit.getInstance().getString(R.string.dt_heading_TDD),
                        FormatKit.getInstance().formatAsInsulin(totalInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                        FormatKit.getInstance().formatAsPercent(bolusPercent),
                        FormatKit.getInstance().formatAsInsulin(bolusInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Basal),
                        FormatKit.getInstance().formatAsPercent(basalPercent),
                        FormatKit.getInstance().formatAsInsulin(basalInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Sensor),
                        sgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(sgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                        FormatKit.getInstance().formatAsGlucose(sgStddev),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsPercent(sgPercentAboveHigh),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationAboveHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Within),
                        FormatKit.getInstance().formatAsPercent(sgPercentWithinLimit),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationWithinLimit),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsPercent(sgPercentBelowLow),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationBelowLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_BG),
                        bgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(bgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsGlucose(meterBgHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsGlucose(meterBgLow),
                        FormatKit.getInstance().getString(R.string.dt_heading_Manual),
                        manualBgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsGlucose(manualBgHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsGlucose(manualBgLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_Wizard),
                        bolusWizardUsageCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Carb),
                        PumpHistoryParser.CARB_UNITS.EXCHANGES.equals(carbUnits) ?
                                FormatKit.getInstance().formatAsExchanges(totalFoodInput) :
                                FormatKit.getInstance().formatAsGrams(totalFoodInput),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food),
                        bolusWizardFoodOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Correct),
                        bolusWizardCorrectionOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsCorrectionOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food_Corr),
                        bolusWizardFoodAndCorrectionBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodAndCorrection),
                        FormatKit.getInstance().getString(R.string.dt_heading_Manual),
                        manualBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalManualBolusInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Alerts),
                        highPredictiveAlerts + highBgAlerts + lowPredictiveAlerts + lowBgAlerts
                                + predictiveLowGlucoseSuspendAlerts + lowGlucoseSuspendAlerts + risingRateAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_PreHigh),
                        highPredictiveAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        highBgAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_PreLow),
                        lowPredictiveAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        lowBgAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_PreSuspd),
                        predictiveLowGlucoseSuspendAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_Suspend),
                        lowGlucoseSuspendAlerts,
                        FormatKit.getInstance().getString(R.string.dt_heading_Rise),
                        risingRateAlerts);

            } else {

                notes += String.format("%s %s" +
                                " | %s %s %s %s %s %s %s %s" +
                                " | %s x%s %s %s %s %s %s %s %s %s %s %s %s %s %s" +
                                " | %s x%s %s %s %s %s %s %s %s %s" +
                                " | %s x%s %s %s %s x%s %s %s x%s %s %s x%s %s %s x%s %s" +
                                " | %s %s %s x%s %s %s %s %s %s %s %s %s",

                        FormatKit.getInstance().getString(R.string.dt_heading_Daily_Totals),
                        sdfDay.format(midday),

                        FormatKit.getInstance().getString(R.string.dt_heading_TDD),
                        FormatKit.getInstance().formatAsInsulin(totalInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                        FormatKit.getInstance().formatAsPercent(bolusPercent),
                        FormatKit.getInstance().formatAsInsulin(bolusInsulin),
                        FormatKit.getInstance().getString(R.string.dt_heading_Basal),
                        FormatKit.getInstance().formatAsPercent(basalPercent),
                        FormatKit.getInstance().formatAsInsulin(basalInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Sensor),
                        sgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(sgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                        FormatKit.getInstance().formatAsGlucose(sgStddev),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsPercent(sgPercentAboveHigh),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationAboveHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Within),
                        FormatKit.getInstance().formatAsPercent(sgPercentWithinLimit),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationWithinLimit),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsPercent(sgPercentBelowLow),
                        FormatKit.getInstance().formatMinutesAsHM(sgDurationBelowLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_BG),
                        bgCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                        FormatKit.getInstance().formatAsGlucose(bgAverage),
                        FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                        FormatKit.getInstance().formatAsGlucose(bgStdDev),
                        FormatKit.getInstance().getString(R.string.dt_heading_High),
                        FormatKit.getInstance().formatAsGlucose(meterBgHigh),
                        FormatKit.getInstance().getString(R.string.dt_heading_Low),
                        FormatKit.getInstance().formatAsGlucose(meterBgLow),

                        FormatKit.getInstance().getString(R.string.dt_heading_Wizard),
                        bolusWizardUsageCount + mealWizardUsageCount,
                        FormatKit.getInstance().getString(R.string.dt_heading_Carb),
                        PumpHistoryParser.CARB_UNITS.EXCHANGES.equals(carbUnits) ?
                                FormatKit.getInstance().formatAsExchanges(totalFoodInput) :
                                FormatKit.getInstance().formatAsGrams(totalFoodInput),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food),
                        bolusWizardFoodOnlyBolusCount + mealWizardFoodOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodOnlyBolus + totalMealWizardInsulinAsFoodOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Correct),
                        bolusWizardCorrectionOnlyBolusCount + mealWizardCorrectionOnlyBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsCorrectionOnlyBolus + totalMealWizardInsulinAsCorrectionOnlyBolus),
                        FormatKit.getInstance().getString(R.string.dt_heading_Food_Corr),
                        bolusWizardFoodAndCorrectionBolusCount + mealWizardFoodAndCorrectionBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalBolusWizardInsulinAsFoodAndCorrection + totalMealWizardInsulinAsFoodAndCorrection),
                        FormatKit.getInstance().getString(R.string.dt_heading_Manual),
                        manualBolusCount,
                        FormatKit.getInstance().formatAsInsulin(totalManualBolusInsulin),

                        FormatKit.getInstance().getString(R.string.dt_heading_Auto),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInCLActiveMode),
                        FormatKit.getInstance().getString(R.string.dt_heading_Micro),
                        microBolusCount,
                        FormatKit.getInstance().formatAsInsulin(microBolusInsulinDelivered),
                        FormatKit.getInstance().getString(R.string.dt_heading_Therapy),
                        FormatKit.getInstance().getString(R.string.dt_heading_therapy_Range),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInTherapyTargetRange),
                        FormatKit.getInstance().getString(R.string.dt_heading_therapy_Above),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInAboveTherapyTargetRangeHiLimit),
                        FormatKit.getInstance().getString(R.string.dt_heading_therapy_Below),
                        FormatKit.getInstance().formatMinutesAsHM(totalTimeInBelowTherapyTargetRangeLowLimit));
            }

        }

        treatment.setNotes(FormatKit.getInstance().asMongoDBIndexKeySafe(notes));

        return nightscoutItems;
    }

    @Override
    public List<MessageItem> message(PumpHistorySender pumpHistorySender, String senderID) {
        List<MessageItem> messageItems = new ArrayList<>();

        long midday = startDate.getTime() + ((endDate.getTime() - startDate.getTime()) / 2);

        String title = String.format("%s %s",
                FormatKit.getInstance().getString(R.string.dt_heading_Daily_Totals),
                FormatKit.getInstance().formatAsDayName(midday));

        String message = String.format("%s %s • %s %s %s • %s %s %s • %s %s • %s %s • %s %s • %s %s • %s %s",
                FormatKit.getInstance().getString(R.string.dt_heading_TDD),
                FormatKit.getInstance().formatAsInsulin(totalInsulin),
                FormatKit.getInstance().getString(R.string.dt_heading_Bolus),
                FormatKit.getInstance().formatAsInsulin(bolusInsulin),
                FormatKit.getInstance().formatAsPercent(bolusPercent),
                FormatKit.getInstance().getString(R.string.dt_heading_Basal),
                FormatKit.getInstance().formatAsInsulin(basalInsulin),
                FormatKit.getInstance().formatAsPercent(basalPercent),
                FormatKit.getInstance().getString(R.string.dt_heading_Avg),
                FormatKit.getInstance().formatAsGlucose(sgAverage),
                FormatKit.getInstance().getString(R.string.dt_heading_StdDev),
                FormatKit.getInstance().formatAsGlucose(sgStddev),
                FormatKit.getInstance().getString(R.string.dt_heading_High),
                FormatKit.getInstance().formatAsPercent(sgPercentAboveHigh),
                FormatKit.getInstance().getString(R.string.dt_heading_Within),
                FormatKit.getInstance().formatAsPercent(sgPercentWithinLimit),
                FormatKit.getInstance().getString(R.string.dt_heading_Low),
                FormatKit.getInstance().formatAsPercent(sgPercentBelowLow));

        if (mealWizardUsageCount > 0) {
            message += String.format(" • %s %s",
                    FormatKit.getInstance().getString(R.string.dt_heading_Food),
                    PumpHistoryParser.CARB_UNITS.EXCHANGES.equals(carbUnits) ?
                            FormatKit.getInstance().formatAsExchanges(totalFoodInput) :
                            FormatKit.getInstance().formatAsGrams(totalFoodInput));
        }

        if (TYPE.DAILY_TOTALS.equals(type)) {
            message += String.format(" • %s %s",
                    FormatKit.getInstance().getString(R.string.dt_heading_Alerts),
                    highPredictiveAlerts + lowPredictiveAlerts + lowBgAlerts + highBgAlerts +
                            risingRateAlerts + fallingRateAlerts + lowGlucoseSuspendAlerts + predictiveLowGlucoseSuspendAlerts);
        } else if (TYPE.CLOSED_LOOP_DAILY_TOTALS.equals(type)) {
            message += String.format(" • %s %s • %s %s",
                    FormatKit.getInstance().getString(R.string.dt_heading_Auto),
                    FormatKit.getInstance().formatMinutesAsHM(totalTimeInCLActiveMode),
                    FormatKit.getInstance().getString(R.string.dt_heading_Micro),
                    microBolusCount);
        }

        messageItems.add(new MessageItem()
                .type(MessageItem.TYPE.DAILY_TOTALS)
                .date(eventDate)
                .title(title)
                .message(message));

        return messageItems;
    }

    public static void dailyTotals(
            PumpHistorySender pumpHistorySender, Realm realm, long pumpMAC,
            Date eventDate, int eventRTC, int eventOFFSET,
            int type,
            Date startDate,
            Date endDate,
            int rtc,
            int offset,
            int duration,
            int meterBgCount,
            int meterBgAverage,
            int meterBgLow,
            int meterBgHigh,
            int manualBgCount,
            int manualBgAverage,
            int manualBgLow,
            int manualBgHigh,
            int bgCount,
            int bgAverage,
            int bgStdDev,
            double totalInsulin,
            double basalInsulin,
            int basalPercent,
            double bolusInsulin,
            int bolusPercent,
            int carbUnits,
            int bolusWizardUsageCount,
            int mealWizardUsageCount,
            double totalFoodInput,
            double totalBolusWizardInsulinAsFoodOnlyBolus,
            double totalBolusWizardInsulinAsCorrectionOnlyBolus,
            double totalBolusWizardInsulinAsFoodAndCorrection,
            double totalMealWizardInsulinAsFoodOnlyBolus,
            double totalMealWizardInsulinAsCorrectionOnlyBolus,
            double totalMealWizardInsulinAsFoodAndCorrection,
            double totalManualBolusInsulin,
            int bolusWizardFoodOnlyBolusCount,
            int bolusWizardCorrectionOnlyBolusCount,
            int bolusWizardFoodAndCorrectionBolusCount,
            int mealWizardFoodOnlyBolusCount,
            int mealWizardCorrectionOnlyBolusCount,
            int mealWizardFoodAndCorrectionBolusCount,
            int manualBolusCount,
            int sgCount,
            int sgAverage,
            int sgStddev,
            int sgDurationAboveHigh,
            int sgPercentAboveHigh,
            int sgDurationWithinLimit,
            int sgPercentWithinLimit,
            int sgDurationBelowLow,
            int sgPercentBelowLow,
            int lgSuspensionDuration,
            int highPredictiveAlerts,
            int lowPredictiveAlerts,
            int lowBgAlerts,
            int highBgAlerts,
            int risingRateAlerts,
            int fallingRateAlerts,
            int lowGlucoseSuspendAlerts,
            int predictiveLowGlucoseSuspendAlerts,
            int microBolusCount,
            double microBolusInsulinDelivered,
            int totalTimeInCLActiveMode,
            int totalTimeInTherapyTargetRange,
            int totalTimeInAboveTherapyTargetRangeHiLimit,
            int totalTimeInBelowTherapyTargetRangeLowLimit) {

        PumpHistoryDaily record = realm.where(PumpHistoryDaily.class)
                .equalTo("pumpMAC", pumpMAC)
                .equalTo("eventRTC", eventRTC)
                .findFirst();
        if (record == null) {
            Log.d(TAG, "*new*" + " daily totals: (event) " + eventDate + " (startDate) " + startDate + " (endDate) " + endDate);
            record = realm.createObject(PumpHistoryDaily.class);
            record.pumpMAC = pumpMAC;
            record.eventDate = eventDate;
            record.key = HistoryUtils.key("DAILY", eventRTC);
            pumpHistorySender.setSenderREQ(record);

            record.eventRTC = eventRTC;
            record.eventOFFSET = eventOFFSET;

            record.type = type;

            record.startDate = startDate;
            record.endDate = endDate;
            record.rtc = rtc;
            record.offset = offset;
            record.duration = duration;

            record.meterBgCount = meterBgCount;
            record.meterBgAverage = meterBgAverage;
            record.meterBgLow = meterBgLow;
            record.meterBgHigh = meterBgHigh;
            record.manualBgCount = manualBgCount;
            record.manualBgAverage = manualBgAverage;
            record.manualBgLow = manualBgLow;
            record.manualBgHigh = manualBgHigh;
            record.bgCount = bgCount;
            record.bgAverage = bgAverage;
            record.bgStdDev = bgStdDev;

            record.totalInsulin = totalInsulin;
            record.basalInsulin = basalInsulin;
            record.basalPercent = basalPercent;
            record.bolusInsulin = bolusInsulin;
            record.bolusPercent = bolusPercent;

            record.carbUnits = carbUnits;
            record.bolusWizardUsageCount = bolusWizardUsageCount;

            record.totalFoodInput = totalFoodInput;
            record.bolusWizardUsageCount = bolusWizardUsageCount;
            record.mealWizardUsageCount = mealWizardUsageCount;
            record.totalBolusWizardInsulinAsFoodOnlyBolus = totalBolusWizardInsulinAsFoodOnlyBolus;
            record.totalBolusWizardInsulinAsCorrectionOnlyBolus = totalBolusWizardInsulinAsCorrectionOnlyBolus;
            record.totalBolusWizardInsulinAsFoodAndCorrection = totalBolusWizardInsulinAsFoodAndCorrection;
            record.totalMealWizardInsulinAsFoodOnlyBolus = totalMealWizardInsulinAsFoodOnlyBolus;
            record.totalMealWizardInsulinAsCorrectionOnlyBolus = totalMealWizardInsulinAsCorrectionOnlyBolus;
            record.totalMealWizardInsulinAsFoodAndCorrection = totalMealWizardInsulinAsFoodAndCorrection;
            record.totalManualBolusInsulin = totalManualBolusInsulin;
            record.bolusWizardFoodOnlyBolusCount = bolusWizardFoodOnlyBolusCount;
            record.bolusWizardCorrectionOnlyBolusCount = bolusWizardCorrectionOnlyBolusCount;
            record.bolusWizardFoodAndCorrectionBolusCount = bolusWizardFoodAndCorrectionBolusCount;
            record.mealWizardFoodOnlyBolusCount = mealWizardFoodOnlyBolusCount;
            record.mealWizardCorrectionOnlyBolusCount = mealWizardCorrectionOnlyBolusCount;
            record.mealWizardFoodAndCorrectionBolusCount = mealWizardFoodAndCorrectionBolusCount;
            record.manualBolusCount = manualBolusCount;

            record.sgCount = sgCount;
            record.sgAverage = sgAverage;
            record.sgStddev = sgStddev;
            record.sgDurationAboveHigh = sgDurationAboveHigh;
            record.sgPercentAboveHigh = sgPercentAboveHigh;
            record.sgDurationWithinLimit = sgDurationWithinLimit;
            record.sgPercentWithinLimit = sgPercentWithinLimit;
            record.sgDurationBelowLow = sgDurationBelowLow;
            record.sgPercentBelowLow = sgPercentBelowLow;
            record.lgSuspensionDuration = lgSuspensionDuration;

            record.highPredictiveAlerts = highPredictiveAlerts;
            record.lowPredictiveAlerts = lowPredictiveAlerts;
            record.lowBgAlerts = lowBgAlerts;
            record.highBgAlerts= highBgAlerts;
            record.risingRateAlerts = risingRateAlerts;
            record.fallingRateAlerts = fallingRateAlerts;
            record.lowGlucoseSuspendAlerts = lowGlucoseSuspendAlerts;
            record.predictiveLowGlucoseSuspendAlerts = predictiveLowGlucoseSuspendAlerts;

            record.microBolusInsulinDelivered = microBolusInsulinDelivered;
            record.microBolusCount= microBolusCount;
            record.totalTimeInCLActiveMode = totalTimeInCLActiveMode;
            record.totalTimeInTherapyTargetRange = totalTimeInTherapyTargetRange;
            record.totalTimeInAboveTherapyTargetRangeHiLimit = totalTimeInAboveTherapyTargetRangeHiLimit;
            record.totalTimeInBelowTherapyTargetRangeLowLimit = totalTimeInBelowTherapyTargetRangeLowLimit;
        }
    }

    public enum TYPE {
        DAILY_TOTALS(1),
        CLOSED_LOOP_DAILY_TOTALS(2),
        NA(-1);

        private int value;

        TYPE(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public boolean equals(int value) {
            return this.value == value;
        }

        public static TYPE convert(int value) {
            for (TYPE TYPE : TYPE.values())
                if (TYPE.value == value) return TYPE;
            return TYPE.NA;
        }
    }

    @Override
    public String getSenderREQ() {
        return senderREQ;
    }

    @Override
    public void setSenderREQ(String senderREQ) {
        this.senderREQ = senderREQ;
    }

    @Override
    public String getSenderACK() {
        return senderACK;
    }

    @Override
    public void setSenderACK(String senderACK) {
        this.senderACK = senderACK;
    }

    @Override
    public String getSenderDEL() {
        return senderDEL;
    }

    @Override
    public void setSenderDEL(String senderDEL) {
        this.senderDEL = senderDEL;
    }

    @Override
    public Date getEventDate() {
        return eventDate;
    }

    @Override
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public long getPumpMAC() {
        return pumpMAC;
    }

    @Override
    public void setPumpMAC(long pumpMAC) {
        this.pumpMAC = pumpMAC;
    }

    public int getEventRTC() {
        return eventRTC;
    }

    public int getEventOFFSET() {
        return eventOFFSET;
    }

    public int getType() {
        return type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getRtc() {
        return rtc;
    }

    public int getOffset() {
        return offset;
    }

    public int getDuration() {
        return duration;
    }

    public int getMeterBgCount() {
        return meterBgCount;
    }

    public int getMeterBgAverage() {
        return meterBgAverage;
    }

    public int getMeterBgLow() {
        return meterBgLow;
    }

    public int getMeterBgHigh() {
        return meterBgHigh;
    }

    public int getManualBgCount() {
        return manualBgCount;
    }

    public int getManualBgAverage() {
        return manualBgAverage;
    }

    public int getManualBgLow() {
        return manualBgLow;
    }

    public int getManualBgHigh() {
        return manualBgHigh;
    }

    public int getBgCount() {
        return bgCount;
    }

    public int getBgAverage() {
        return bgAverage;
    }

    public int getBgStdDev() {
        return bgStdDev;
    }

    public double getTotalInsulin() {
        return totalInsulin;
    }

    public double getBasalInsulin() {
        return basalInsulin;
    }

    public int getBasalPercent() {
        return basalPercent;
    }

    public double getBolusInsulin() {
        return bolusInsulin;
    }

    public int getBolusPercent() {
        return bolusPercent;
    }

    public int getCarbUnits() {
        return carbUnits;
    }

    public int getBolusWizardUsageCount() {
        return bolusWizardUsageCount;
    }

    public int getMealWizardUsageCount() {
        return mealWizardUsageCount;
    }

    public double getTotalFoodInput() {
        return totalFoodInput;
    }

    public double getTotalBolusWizardInsulinAsFoodOnlyBolus() {
        return totalBolusWizardInsulinAsFoodOnlyBolus;
    }

    public double getTotalBolusWizardInsulinAsCorrectionOnlyBolus() {
        return totalBolusWizardInsulinAsCorrectionOnlyBolus;
    }

    public double getTotalBolusWizardInsulinAsFoodAndCorrection() {
        return totalBolusWizardInsulinAsFoodAndCorrection;
    }

    public double getTotalMealWizardInsulinAsFoodOnlyBolus() {
        return totalMealWizardInsulinAsFoodOnlyBolus;
    }

    public double getTotalMealWizardInsulinAsCorrectionOnlyBolus() {
        return totalMealWizardInsulinAsCorrectionOnlyBolus;
    }

    public double getTotalMealWizardInsulinAsFoodAndCorrection() {
        return totalMealWizardInsulinAsFoodAndCorrection;
    }

    public double getTotalManualBolusInsulin() {
        return totalManualBolusInsulin;
    }

    public int getBolusWizardFoodOnlyBolusCount() {
        return bolusWizardFoodOnlyBolusCount;
    }

    public int getBolusWizardCorrectionOnlyBolusCount() {
        return bolusWizardCorrectionOnlyBolusCount;
    }

    public int getBolusWizardFoodAndCorrectionBolusCount() {
        return bolusWizardFoodAndCorrectionBolusCount;
    }

    public int getMealWizardFoodOnlyBolusCount() {
        return mealWizardFoodOnlyBolusCount;
    }

    public int getMealWizardCorrectionOnlyBolusCount() {
        return mealWizardCorrectionOnlyBolusCount;
    }

    public int getMealWizardFoodAndCorrectionBolusCount() {
        return mealWizardFoodAndCorrectionBolusCount;
    }

    public int getManualBolusCount() {
        return manualBolusCount;
    }

    public int getSgCount() {
        return sgCount;
    }

    public int getSgAverage() {
        return sgAverage;
    }

    public int getSgStddev() {
        return sgStddev;
    }

    public int getSgDurationAboveHigh() {
        return sgDurationAboveHigh;
    }

    public int getSgPercentAboveHigh() {
        return sgPercentAboveHigh;
    }

    public int getSgDurationWithinLimit() {
        return sgDurationWithinLimit;
    }

    public int getSgPercentWithinLimit() {
        return sgPercentWithinLimit;
    }

    public int getSgDurationBelowLow() {
        return sgDurationBelowLow;
    }

    public int getSgPercentBelowLow() {
        return sgPercentBelowLow;
    }

    public int getLgSuspensionDuration() {
        return lgSuspensionDuration;
    }

    public int getHighPredictiveAlerts() {
        return highPredictiveAlerts;
    }

    public int getLowPredictiveAlerts() {
        return lowPredictiveAlerts;
    }

    public int getLowBgAlerts() {
        return lowBgAlerts;
    }

    public int getHighBgAlerts() {
        return highBgAlerts;
    }

    public int getRisingRateAlerts() {
        return risingRateAlerts;
    }

    public int getFallingRateAlerts() {
        return fallingRateAlerts;
    }

    public int getLowGlucoseSuspendAlerts() {
        return lowGlucoseSuspendAlerts;
    }

    public int getPredictiveLowGlucoseSuspendAlerts() {
        return predictiveLowGlucoseSuspendAlerts;
    }

    public int getMicroBolusCount() {
        return microBolusCount;
    }

    public double getMicroBolusInsulinDelivered() {
        return microBolusInsulinDelivered;
    }

    public int getTotalTimeInCLActiveMode() {
        return totalTimeInCLActiveMode;
    }

    public int getTotalTimeInTherapyTargetRange() {
        return totalTimeInTherapyTargetRange;
    }

    public int getTotalTimeInAboveTherapyTargetRangeHiLimit() {
        return totalTimeInAboveTherapyTargetRangeHiLimit;
    }

    public int getTotalTimeInBelowTherapyTargetRangeLowLimit() {
        return totalTimeInBelowTherapyTargetRangeLowLimit;
    }
}
