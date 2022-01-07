const axios = require('axios')
const crypto = require('crypto')

async function setSessionCount(port, newCount) {
  const currentCount = await getSessionCount(port);

  console.log(`Requested ${newCount} entries. Current count ${currentCount}`)

  if(newCount < currentCount) {
    await deleteSessions(port,  currentCount - newCount)
  } else if (currentCount < newCount){
    await createSessions(port, newCount - currentCount)
  } else  {

    console.log(`The counts match, doing nothing.`)
  }
}

async function getSessionCount(port) {
  const response = await axios.get(`http://localhost:${port}/sessions`)
  return response.data.total
}

async function createSessions(port, num) {
  console.log(`Creating ${num} entries`)
  let i = 0
  while(i < num) {
    const sessionId = crypto.randomUUID()
    const response = await axios.put(`http://localhost:${port}/sessions/${sessionId}`)
    i++;
  }
}

async function deleteSessions(port, num) {
  console.log(`Deleting ${num} entries`)
  let i = 0
  let response = await axios.get(`http://localhost:${port}/sessions`)
  while(i < num && response.data.total > 0) {
    let k = 0
    while(i + k < num && k < response.data.items.length) {
      const sessionId = response.data.items[k]
      await axios.delete(`http://localhost:${port}/sessions/${sessionId}`)
      k++;
    }
    i += k;
    response = await axios.get(`http://localhost:${port}/sessions`)
  }
}

let argv = process.argv.slice(2)
const port = argv[0]
const newCount = argv[1]

console.log(`Starting job, args are: ${argv}`)

setSessionCount(port, newCount)

