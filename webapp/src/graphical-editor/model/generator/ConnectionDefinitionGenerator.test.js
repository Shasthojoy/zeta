import ConnectionDefinitionGenerator from './ConnectionDefinitionGenerator';

const defaultTestId = "1234"

describe('createLabel', () => {
    function create(connections) {
        const generator = new ConnectionDefinitionGenerator(connections)
        return generator;
    }

    test('with empty connetions', () => {
      const generator = create([])

      expect(generator.getLabels('Connection1')).toEqual([])
    })

    test('with empty placing', () => {
      const connections = [
        {
          "name": "Connection1",
          "placings": []
        }
      ]

      const generator = create(connections)

      expect(generator.getLabels('Connection1')).toEqual([])
    })

    test('with one full defined connection and one placing', () => {
        const connections = [
                {
                  "name": "Connection1",
                  "placings": [
                    {
                      "positionDistance": 1,
                      "positionOffset": 1.0,
                      "shape": {
                        "textBody": "Hallo",
                        "id": defaultTestId,
                        "type": "Label",
                      }
                    }
                  ]
                }
              ]
                
        const generator = create(connections)

        expect(generator.getLabels('Connection1')).toEqual(
            [{
                position: 1.0,
                attrs: {
                    rect: {fill: 'transparent'},
                    text: {
                    y: 1,
                    text: "Hallo"
                    }
                },
                id: defaultTestId
            }]
        )
    }) 

    test('with one full defined connection and two placing', () => {
      const connections = [
              {
                "name": "Connection1",
                "placings": [
                  {
                    "positionDistance": 1,
                    "positionOffset": 1.0,
                    "shape": {
                      "textBody": "Hallo",
                      "id": "placing1",
                      "type": "Label",
                    }
                  },
                  {
                    "positionDistance": 1,
                    "positionOffset": 1.0,
                    "shape": {
                      "textBody": "Hallo",
                      "id": "placing2",
                      "type": "Label",
                    }
                  }
                ]
              }
            ]
              
      const generator = create(connections)

      expect(generator.getLabels('Connection1')).toEqual(
          [{
              position: 1.0,
              attrs: {
                  rect: {fill: 'transparent'},
                  text: {
                  y: 1,
                  text: "Hallo"
                  }
              },
              id: "placing1"
          },
          {
            position: 1.0,
            attrs: {
                rect: {fill: 'transparent'},
                text: {
                y: 1,
                text: "Hallo"
                }
            },
            id: "placing2"
        }]
      )
  })
});

describe('createPlacing', () => {
  function create(connections) {
    const generator = new ConnectionDefinitionGenerator(connections)
    return generator;
  }

  function createDefaultTestConnection(placing) {
    return [
      {
        "name": "Connection1",
        "placings": [
          placing
        ]
      }
    ]
  }

  test('with empty connetions', () => {
    const generator = create([])

    expect(generator.getPlacings('Connection1')).toEqual([])
  })

  test('with empty placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": []
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual([])
  })

  test('with a Line as Shape with a Style', () => {
    const connections = createDefaultTestConnection(
      {
        "positionOffset": 1.0,
        "shape": {
          "endPoint": {
            "x": 0,
            "y": 0,
          },
          "startPoint": {
            "x": 0,
            "y": 0,
          },
          "style": "Style1",
          "type": "Line",
        }
      }
    )

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<line />',
          attrs: {
              x1: 0,
              y1: 0,
              x2: 0,
              y2: 0,
              style: "dummy"
          }
        }
    ])
  })
});