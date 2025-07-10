import { MemoryStorage, ConversationState } from 'botbuilder';
import { CosmosDbPartitionedStorage } from 'botbuilder-azure';

const useMockDb = process.env.USE_MOCK_DB === 'true';

export const storage = useMockDb
  ? new MemoryStorage()
  : new CosmosDbPartitionedStorage({
      cosmosDbEndpoint: process.env.COSMOS_DB_ENDPOINT!,
      authKey: process.env.COSMOS_DB_KEY!,
      databaseId: process.env.COSMOS_DB_DATABASE!,
      containerId: process.env.COSMOS_DB_CONTAINER!
    });

export const conversationState = new ConversationState(storage);
export const userStateAccessor = conversationState.createProperty<any>('UserConversationState');
