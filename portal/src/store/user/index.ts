import actions from './actions';
import getters from './getters';
import mutations from './mutation';

const state = {
	'agencyCode': null, // 机构code
	'userInfo': null,
	'tokenID': ``,
	'refreshTokenID': ``,
	'powerList': [],
	'sysId': null,
	'powerAliasTree': null,
	'routerList': null
};
export default {
	'namespaced': true,
	state,
	getters,
	actions,
	mutations
};
