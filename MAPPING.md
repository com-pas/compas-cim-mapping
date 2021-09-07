<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# CIM Mapping

Below is an overview of the mapping done between IEC CIM Classes and IEC 61850 Classes and their properties.  
There is an IEC document describing the mapping, namely IEC/TS 62361-102, but not all details are in there.

## Mapping IEC CIM to IEC 61850

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:Substation*                 | *TSubstation*                    |           |
| id                               | name                             |           |
| name                             | desc                             |           |
| List&lt;cim:VoltageLevel&gt;     | List&lt;TVoltageLevel&gt;        | (1)       |
| List&lt;*PowerTransformer*&gt;   | List&lt;TPowerTransformer&gt;    |           |

(1): The list of VoltageLevels that belong to the Substation.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:VoltageLevel*               | *TVoltageLevel*                  |           |
| name or id                       | name                             |           |
| '0'                              | nomFreq                          | (1)       |
| nominalV                         | voltage.value                    |           |
| 'k'                              | voltage.multiplier               |           |
| 'V'                              | voltage.unit                     |           |
| List&lt;Bay&gt;                  | List&lt;TBay&gt;                 | (2)       |
| List&lt;*PowerTransformer*&gt;   | List&lt;TPowerTransformer&gt;    |           |

(1): The nomFreq will be set to 0 if there is a Switch connected to it of the type 'DCLineSegment'.  
(2): The list of Bays that belong to the VoltageLevel.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:Bay*                        | *TBay*                           |           |
| name or id                       | name                             |           |
| List&lt;cim:ConnectivityNode&gt; | List&lt;TConnectivityNode&gt;    | (1)       |
| List&lt;*Switches*&gt;           | List&lt;TConductingEquipment&gt; | (2)       |
| List&lt;*PowerTransformer*&gt;   | List&lt;TPowerTransformer&gt;    |           |

(1): ConnectivityNode in IEC CIM can be linked to a Bay, but also to the VoltageLevel. In IEC 61850 a ConnectivityNode
can only be added to a Bay, so also the ConnectivityNode from the VoltageLevel are added to the Bay. This causes those
ConnectivityNode to be found under each Bay of that VoltageLevel.  
(2): Switches in IEC CIM can be the following types, cim:Switch cim:Breaker cim:Disconnector cim:LoadBreakSwitch cim:
ProtectedSwitch.These classes are all mapped in the same way on IEC 61850

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:PowerTransformer*           | *TPowerTransformer*              |           |
| name or id                       | name                             |           |
| description                      | desc                             |           |
| 'PTR'                            | type                             |           |

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:PowerTransformerEnd*        | *TTransformerWinding*            |           |
| name or id                       | name                             |           |
| 'PTW'                            | type                             |           |
| Terminal                         | List&lt;TTerminal&gt;            | (1)       |

(1): The terminal found in IEC CIM will be added as List to IEC 61850.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:RatioTapChanger*            | *TTapChanger*                    |           |
| *cim:PhaseTapChanger*            | *TTapChanger*                    |           |
| name or id                       | name                             |           |
| 'LTC'                            | type                             |           |

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:ConnectivityNode*           | *TConnectivityNode*              |           |
| name or id                       | name                             |           |
| -                                | pathName                         | (1)       |

(1): The path name is derived from the names of all parent of that ConnectivityNode. In the context we keep a list of
all Naming Elements we passed before coming to that ConnectivityNode. In this way we can build the PathName of that
ConnectivityNode.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *Switches*                       | *TConductingEquipment*           | (1)       |
| name or id                       | name                             |           |
| type                             | type                             | (2)       |
| List&lt;cim:Terminal&gt;         | List&lt;TTerminal&gt;            | (3)       |

(1): Switches in IEC CIM can be the following types, cim:Switch cim:Breaker cim:Disconnector cim:LoadBreakSwitch cim:
ProtectedSwitch.These classes are all mapped in the same way on IEC 61850  
(2): The mapping between types is described in 5.6.2 of IEC/TS 62361-102. Below is a table describing how the mapper
implements this mapping.  
(3): The list of Terminal that belong to the Switch.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:Terminal*                   | *TTerminal*                      |           |
| name or id                       | name                             |           |
| -                                | connectivityNode                 | (1)       |
| -                                | CNodeName                        | (1)       |

(1): Use the ID of the ConnectivityNode to find the name or pathName of that ConnectivityNode. A map is saved of all
ConnectivityNode that are processed for each Bay.

## Mapping from Cim Switch Type to IEC TConductingEquipment

| IEC 61850 Type  | CIM Type              |
| --------------- | --------------------- |
| BSH             | Connector             |
| CAB             | ACLineSegment         |
| CAB             | DCLineSegment         |
| CAP             | ShuntCompensator      |
| CAP             | SeriesCompensator     |
| CBR             | ProtectedSwitch       |
| CBR             | Breaker               |
| CBR             | Recloser              |
| CON             | FrequencyConverter    |
| CTR             | CurrentTransformer    |
| DIS             | Switch                |
| DIS             | Disconnector          |
| DIS             | Fuse                  |
| DIS             | Jumper                |
| DIS             | LoadBreakSwitch       |
| DIS             | GroundDisconnector    |
| DIS             | Sectionaliser         |
| EFN             | PetersenCoil          |
| GEN             | GeneratingUnit        |
| LTC             | TapChanger            |
| LTC             | RatioTapChanger       |
| LTC             | PhaseTapChanger       |
| MOT             | AsynchronousMachine   |
| PSH             | GroundingImpedance    |
| PTR             | PowerTransformer      |
| PTW             | TransformerEnd        |
| PTW             | PowerTransformerEnd   |
| PTW             | TransformerTankEnd    |
| RES             | EarthFaultCompensator |
| SAR             | SurgeArrester         |
| SCR             | ACDCConverter         |
| SMC             | SynchronousMachine    |
| TCR             | StaticVarCompensator  |
| TNK             | TransformerTank       |
| VTR             | PotentialTransformer  |
