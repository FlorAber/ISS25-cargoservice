const { MongoClient } = require('mongodb');

async function main() {
  const uri = 'mongodb://localhost:27017'; 

  const client = new MongoClient(uri);

  try {
    // Connessione al server MongoDB
    await client.connect();
    console.log('✅ Connesso a MongoDB');

    const db = client.db('cargodb');

    const collection = db.collection('products');


    const productsToInsert = [
      {
        productId: 1,
        jsonRep: "{\"productId\":1,\"name\":\"p1\",\"weight\":10}"
      },
      {
        productId: 2,
        jsonRep: "{\"productId\":2,\"name\":\"p2\",\"weight\":20}" 
      },
      {
        productId: 3,
        jsonRep: "{\"productId\":3,\"name\":\"p3\",\"weight\":30}"
      },
      {
        productId: 4,
        jsonRep: "{\"productId\":4,\"name\":\"p4\",\"weight\":40}"
      },
      {
        productId: 5,
        jsonRep: "{\"productId\":5,\"name\":\"p5\",\"weight\":200}"
      }
    ];

    // Inserisce tutti i prodotti nell'array
    const result = await collection.insertMany(productsToInsert);
    console.log(`Inseriti ${result.insertedCount} prodotti.`);
    console.log("ID dei prodotti inseriti:");
    // Itera sugli ID inseriti per stamparli
    for (const key in result.insertedIds) {
      console.log(`  - ${result.insertedIds[key]}`);
    }
  } catch (err) {
    console.error(' Errore:', err);
  } finally {
    // Chiudi la connessione
    await client.close();
    console.log(' Connessione chiusa');
  }
}

main();