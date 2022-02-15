<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# CIM Mapping

Below is an overview of the mapping done between IEC CIM Classes and IEC 61850 Classes and their properties.  
There is an IEC document describing the mapping, namely IEC/TS 62361-102, but not all details are in there.

## Mapping IEC CIM to IEC 61850

**Remark**: In CIM the name is optional, so when the name is not there the ID is used. This is the case for many
entities described below (``name or id``).

| CIM Class                        | IEC Class                     | Required | Remark |
|----------------------------------|-------------------------------|----------|--------|
| *cim:Substation*                 | *TSubstation*                 | -        |        |
| id                               | name                          | Yes      |        |
| name                             | desc                          | No       |        |
| List&lt;cim:VoltageLevel&gt;     | List&lt;TVoltageLevel&gt;     | -        | (1)    |
| List&lt;cim:PowerTransformer&gt; | List&lt;TPowerTransformer&gt; | -        |        |

(1): The list of VoltageLevels that belong to the Substation.

| CIM Class                        | IEC Class                     | Required | Remark |
|----------------------------------|-------------------------------|----------|--------|
| *cim:VoltageLevel*               | *TVoltageLevel*               | -        |        |
| name or id                       | name                          | Yes      |        |
| '0'                              | nomFreq                       | -        | (1)    |
| nominalV                         | voltage.value                 | No       |        |
| 'k'                              | voltage.multiplier            | -        |        |
| 'V'                              | voltage.unit                  | -        |        |
| List&lt;cim:Bay&gt;              | List&lt;TBay&gt;              | -        | (2)    |
| List&lt;cim:PowerTransformer&gt; | List&lt;TPowerTransformer&gt; | -        |        |

(1): The nomFreq will be set to 0 if there is a Switch connected to it of the type 'DCLineSegment'.  
(2): The list of Bays that belong to the VoltageLevel.

| CIM Class                        | IEC Class                     | Required | Remark |
|----------------------------------|-------------------------------|----------|--------|
| *cim:BusbarSection*              | *TBay*                        | -        |        |
| name or id                       | name                          | Yes      |        |
| List&lt;cim:ConnectivityNode&gt; | List&lt;TConnectivityNode&gt; | -        | (1)    |

(1): The ConnectivityNodes that are linked to the BusbarSection through a Terminal of the BusbarSection.

| CIM Class                        | IEC Class                        | Required | Remark |
|----------------------------------|----------------------------------|----------|--------|
| *cim:Bay*                        | *TBay*                           | -        |        |
| name or id                       | name                             | Yes      |        |
| List&lt;cim:ConnectivityNode&gt; | List&lt;TConnectivityNode&gt;    | -        | (1)    |
| List&lt;*Switches*&gt;           | List&lt;TConductingEquipment&gt; | -        | (2)    |
| List&lt;cim:PowerTransformer&gt; | List&lt;TPowerTransformer&gt;    | -        |        |

(1): The ConnectivityNodes that are linked to the Bay thought the terminals of the switches of the Bay.  
(2): Switches in IEC CIM can be the following types, cim:Switch, cim:Breaker, cim:Disconnector, cim:LoadBreakSwitch and
cim:ProtectedSwitch. These classes are all mapped in the same way on IEC 61850

| CIM Class                           | IEC Class                       | Required | Remark |
|-------------------------------------|---------------------------------|----------|--------|
| *cim:PowerTransformer*              | *TPowerTransformer*             | -        |        |
| name or id                          | name                            | Yes      |        |
| description                         | desc                            | No       |        |
| 'PTR'                               | type                            | -        |        |
| List&lt;cim:PowerTransformerEnd&gt; | List&lt;TTransformerWinding&gt; | -        |        |

| CIM Class                       | IEC Class             | Required | Remark |
|---------------------------------|-----------------------|----------|--------|
| *cim:PowerTransformerEnd*       | *TTransformerWinding* | -        |        |
| name or id (PowerTransformer) + |                       |          |        |
| '_' + endNumber                 | name                  | Yes      |        | 
| 'PTW'                           | type                  | -        |        |
| cim:RatioTapChanger or          |                       |          |        |
| cim:PhaseTapChanger             | tapChanger            | -        |        |
| Terminal                        | List&lt;TTerminal&gt; | -        | (1)    |

(1): The terminal found in IEC CIM will be added as List to IEC 61850.

| CIM Class                | IEC Class     | Required | Remark |
|--------------------------|---------------|----------|--------|
| *cim:RatioTapChanger* or |               |          |        |
| *cim:PhaseTapChanger*    | *TTapChanger* | -        |        |
| name or id               | name          | Yes      |        |
| 'LTC'                    | type          | -        |        |

| CIM Class              | IEC Class           | Required | Remark |
|------------------------|---------------------|----------|--------|
| *cim:ConnectivityNode* | *TConnectivityNode* | -        |        |
| name or id             | name                | Yes      |        |
| -                      | pathName            | -        | (1)    |

(1): The path name is derived from the names of all parent of that ConnectivityNode. In the context we keep a list of
all Naming Elements we passed before coming to that ConnectivityNode. In this way we can build the PathName of that
ConnectivityNode.

| CIM Class                | IEC Class              | Required | Remark |
|--------------------------|------------------------|----------|--------|
| *Switches*               | *TConductingEquipment* | -        | (1)    |
| name or id               | name                   | Yes      |        |
| type                     | type                   | Yes      | (2)    |
| List&lt;cim:Terminal&gt; | List&lt;TTerminal&gt;  | -        | (3)    |

(1): Switches in IEC CIM can be the following types, cim:Switch cim:Breaker cim:Disconnector cim:LoadBreakSwitch cim:
ProtectedSwitch.These classes are all mapped in the same way on IEC 61850  
(2): The mapping between types is described in 5.6.2 of IEC/TS 62361-102. Below is a table describing how the mapper
implements this mapping.  
(3): The list of Terminal that belong to the Switch.

| CIM Class      | IEC Class        | Required | Remark |
|----------------|------------------|----------|--------|
| *cim:Terminal* | *TTerminal*      | -        |        |
| name or id     | name             | Yes      |        |
| -              | connectivityNode | -        | (1)    |
| -              | CNodeName        | -        | (1)    |

(1): Use the ID of the ConnectivityNode to find the name or pathName of that ConnectivityNode. A map is saved of all
ConnectivityNode that are processed for each Bay.

## Mapping from Cim Switch Type to IEC TConductingEquipment

| IEC 61850 Type | CIM Type              |
|----------------|-----------------------|
| BSH            | Connector             |
| CAB            | ACLineSegment         |
| CAB            | DCLineSegment         |
| CAP            | ShuntCompensator      |
| CAP            | SeriesCompensator     |
| CBR            | ProtectedSwitch       |
| CBR            | Breaker               |
| CBR            | Recloser              |
| CON            | FrequencyConverter    |
| CTR            | CurrentTransformer    |
| DIS            | Switch                |
| DIS            | Disconnector          |
| DIS            | Fuse                  |
| DIS            | Jumper                |
| DIS            | LoadBreakSwitch       |
| DIS            | GroundDisconnector    |
| DIS            | Sectionaliser         |
| EFN            | PetersenCoil          |
| GEN            | GeneratingUnit        |
| LTC            | TapChanger            |
| LTC            | RatioTapChanger       |
| LTC            | PhaseTapChanger       |
| MOT            | AsynchronousMachine   |
| PSH            | GroundingImpedance    |
| PTR            | PowerTransformer      |
| PTW            | TransformerEnd        |
| PTW            | PowerTransformerEnd   |
| PTW            | TransformerTankEnd    |
| RES            | EarthFaultCompensator |
| SAR            | SurgeArrester         |
| SCR            | ACDCConverter         |
| SMC            | SynchronousMachine    |
| TCR            | StaticVarCompensator  |
| TNK            | TransformerTank       |
| VTR            | PotentialTransformer  |
