# VoiceXML Reading Exercises

Go through each VoiceXML document, discussing the behavior you would expect from

---

## Exercise 1 — Flight Status IVR
*SkyBridge Airlines*

Walk through this VoiceXML document.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<vxml version="2.1" xmlns="http://www.w3.org/2001/vxml">

  <form id="get_flight_info">

    <field name="flight_number">
      <prompt>Welcome to SkyBridge Airlines. Please enter your flight number.</prompt>
      <grammar type="application/srgs+xml" src="flight_numbers.grxml"/>

      <catch event="noinput" count="1">
        <prompt>Please enter your four-digit flight number.</prompt>
        <reprompt/>
      </catch>
      <catch event="noinput" count="2">
        <prompt>We are unable to process your request. Goodbye.</prompt>
        <exit/>
      </catch>
      <catch event="nomatch">
        <prompt>I did not recognize that flight number. Please try again.</prompt>
        <reprompt/>
      </catch>
    </field>

    <field name="travel_date">
      <prompt>Please enter your travel date as an eight-digit number,
              month, day, and year.</prompt>
      <grammar type="application/srgs+xml" src="dates.grxml"/>

      <catch event="noinput">
        <prompt>Please enter the date as eight digits.</prompt>
        <reprompt/>
      </catch>
    </field>

    <block>
      <submit next="https://api.skybridge.com/status"
              method="post"
              namelist="flight_number travel_date"/>
    </block>

  </form>

</vxml>
```

---

## Exercise 2 — Account Balance Menu
*First Meridian Bank*

Walk through this VoiceXML document.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<vxml version="2.1" xmlns="http://www.w3.org/2001/vxml">

  <var name="account_type" expr="''"/>

  <form id="main_menu">

    <field name="menu_choice">
      <prompt>Thank you for calling First Meridian Bank.
              For checking, press 1. For savings, press 2.
              For credit card, press 3.</prompt>
      <grammar type="application/srgs+xml" src="menu.grxml"/>

      <catch event="nomatch">
        <prompt>Please press 1 for checking, 2 for savings, or 3 for credit card.</prompt>
        <reprompt/>
      </catch>
    </field>

    <filled>
      <assign name="account_type" expr="menu_choice"/>
      <if cond="menu_choice == '1' || menu_choice == '2'">
        <goto next="#deposit_account"/>
      <else/>
        <goto next="#credit_account"/>
      </if>
    </filled>

  </form>

  <form id="deposit_account">
    <field name="account_number">
      <prompt>Please enter your
              <value expr="account_type == '1' ? 'checking' : 'savings'"/>
              account number.</prompt>
      <grammar type="application/srgs+xml" src="digits.grxml"/>
    </field>
    <block>
      <submit next="https://api.firstmeridian.com/balance"
              method="post"
              namelist="account_type account_number"/>
    </block>
  </form>

  <form id="credit_account">
    <field name="card_number">
      <prompt>Please enter your sixteen-digit card number.</prompt>
      <grammar type="application/srgs+xml" src="digits.grxml"/>
    </field>
    <block>
      <submit next="https://api.firstmeridian.com/credit"
              method="post"
              namelist="account_type card_number"/>
    </block>
  </form>

</vxml>
```

---

## Exercise 3 — Appointment Reminder Confirmation
*Lakeview Medical*

Something is wrong with this VoiceXML document. What is it?

```xml
<?xml version="1.0" encoding="UTF-8"?>
<vxml version="2.1" xmlns="http://www.w3.org/2001/vxml">

  <form id="confirm_appointment">

    <field name="confirmation">
      <prompt>This is Lakeview Medical calling to confirm your appointment
              tomorrow at two PM with Doctor Ellis.
              Press 1 to confirm or 2 to cancel.</prompt>
      <grammar type="application/srgs+xml" src="yesno.grxml"/>

      <catch event="noinput">
        <prompt>We were unable to reach you. We will try again later. Goodbye.</prompt>
        <exit/>
      </catch>
      <catch event="nomatch">
        <prompt>Please press 1 to confirm or 2 to cancel.</prompt>
        <reprompt/>
      </catch>
    </field>

    <block>
      <if cond="confirmation == '1'">
        <goto next="confirmed.vxml"/>
      <else/>
        <goto next="cancelled.vxml"/>
      </if>
    </block>

  </form>

</vxml>
```

---

## Exercise 4 — Outage Reporting IVR
*CentralGrid Power*

Something is wrong with this VoiceXML document. What is it?

```xml
<?xml version="1.0" encoding="UTF-8"?>
<vxml version="2.1" xmlns="http://www.w3.org/2001/vxml">

  <form id="report_outage">

    <field name="zip_code">
      <prompt>Thank you for calling CentralGrid Power.
              Please enter the five-digit zip code for the outage location.</prompt>
      <grammar type="application/srgs+xml" src="digits.grxml"/>

      <catch event="noinput">
        <prompt>Please enter your five-digit zip code.</prompt>
        <reprompt/>
      </catch>
    </field>

    <field name="outage_address">
      <prompt>Please enter the street number of the affected address.</prompt>
      <grammar type="application/srgs+xml" src="digits.grxml"/>

      <catch event="noinput">
        <prompt>Please enter the street number.</prompt>
        <reprompt/>
      </catch>
    </field>

    <field name="pole_number">
      <prompt>If you can see a utility pole near the outage,
              enter the pole number. Otherwise press pound to skip.</prompt>
      <grammar type="application/srgs+xml" src="digits_or_skip.grxml"/>
    </field>

    <block>
      <submit next="https://api.centralgrid.com/outage"
              method="post"
              namelist="zip_code pole_number"/>
    </block>

  </form>

</vxml>
```
