
Send Request:

http://localhost:7071/api/InboundRequest

header: Tenant - clientA

body:

[
	{
		"data1": "value1",
		"data2": "value2",
		"data3": "value3"
	},
	{
		"data1": "value11",
		"data2": "value21",
		"data3": "value31"
	},
	{
		"data1": "value12",
		"data2": "value22",
		"data3": "value32",
        "data4": "value32"
	}
]


response:


4d3448e0-d47d-11ed-afa1-0242ac120002




GetStatus:

http://localhost:7071/api/InboundStatus?instanceId=ad8ca59e-828e-41aa-affc-d829f490dd0a

