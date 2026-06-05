import { api } from "./api";

const resourceUrl = (schema: string, entity: string) => `/${schema}/${entity}`;

export const listResource = (schema: string, entity: string) =>
  api.get(resourceUrl(schema, entity));

export const getResource = (schema: string, entity: string, id: number) =>
  api.get(`${resourceUrl(schema, entity)}/${id}`);

export const createResource = (
  schema: string,
  entity: string,
  payload: unknown,
) => api.post(resourceUrl(schema, entity), payload);

export const updateResource = (
  schema: string,
  entity: string,
  id: number,
  payload: unknown,
) => api.put(`${resourceUrl(schema, entity)}/${id}`, payload);

export const deleteResource = (schema: string, entity: string, id: number) =>
  api.delete(`${resourceUrl(schema, entity)}/${id}`);
