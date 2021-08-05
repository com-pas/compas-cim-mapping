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

(1): The list of VoltageLevels that belong to the Substation.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:VoltageLevel*               | *TVoltageLevel*                  |           |
| name or id                       | name                             |           |
| nominalV                         | voltage.value                    |           |
| List&lt;Bay&gt;                  | List&lt;TBay&gt;                 | (1)       |

(1): The list of Bays that belong to the VoltageLevel.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:Bay*                        | *TBay*                           |           |
| name or id                       | name                             |           |
| List&lt;cim:ConnectivityNode&gt; | List&lt;TConnectivityNode&gt;    | (1)       |
| List&lt;*Switches*&gt;           | List&lt;TConductingEquipment&gt; | (2)       |

(1): ConnectivityNode in IEC CIM can be linked to a Bay, but also to the VoltageLevel. In IEC 61850 a ConnectivityNode
can only be added to a Bay, so also the ConnectivityNode from the VoltageLevel are added to the Bay. This causes those
ConnectivityNode to be found under each Bay of that VoltageLevel.  
(2): Switches in IEC CIM can be the following types, cim:Switch cim:Breaker cim:Disconnector cim:LoadBreakSwitch cim:
ProtectedSwitch.These classes are all mapped in the same way on IEC 61850

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:ConnectivityNode*           | *TConnectivityNode               |           |
| name or id                       | name                             |           |
| -                                | pathName                         | (1)       |

(1): The path name is derived from the names of all parent of that ConnectivityNode. In the context we keep a list of
all Naming Elements we passed before coming to that ConnectivityNode. In this way we can build the PathName of that
ConnectivityNode.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *Switches*                       | *TConductingEquipment            | (1)       |
| name or id                       | name                             |           |
| type                             | type                             | (2)       |
| List&lt;cim:Terminal&gt;         | List&lt;TTerminal&gt;            | (3)       |

(1): Switches in IEC CIM can be the following types, cim:Switch cim:Breaker cim:Disconnector cim:LoadBreakSwitch cim:
ProtectedSwitch.These classes are all mapped in the same way on IEC 61850
(2): The mapping between types is described in 5.6.2 of IEC/TS 62361-102.
(3): The list of Terminal that belong to the Switch.

| CIM Class                        | IEC Class                        | Remark    |
| -------------------------------- | -------------------------------- | --------- |
| *cim:Terminal*                   | *TTerminal                       |           |
| name or id                       | name                             |           |
| -                                | connectivityNode                 | (1)       |
| -                                | CNodeName                        | (1)       |

(1): Use the ID of the ConnectivityNode to find the name or pathName of that ConnectivityNode. A map is saved of all
ConnectivityNode that are processed for each Bay.
